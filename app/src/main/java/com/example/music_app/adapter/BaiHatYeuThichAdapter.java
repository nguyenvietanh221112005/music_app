package com.example.music_app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music_app.R;
import com.example.music_app.model.BaiHat;
import com.example.music_app.service.DataService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Là cầu nối giữa dữ liệu (danh sách bài hát yêu thích từ server) và giao diện hiển thị trong RecyclerView.
public class BaiHatYeuThichAdapter extends RecyclerView.Adapter<BaiHatYeuThichAdapter.ViewHolder> {

    // Interface  định nghĩa hành vi callback.
    public interface OnSongClickListener {
        void onSongClick(BaiHat song, int position);//khi nào người dùng click vào bài hát, thì hãy implement (cài đặt) interface này.”
    }

    private final Context context;
    private final ArrayList<BaiHat> danhSachBaiHat;
    private final DataService dataService;
    private final int userId;
    private final OnSongClickListener listener;

    public BaiHatYeuThichAdapter(Context context, ArrayList<BaiHat> danhSachBaiHat,
                                 DataService dataService, int userId, OnSongClickListener listener) {
        this.context = context;
        this.danhSachBaiHat = danhSachBaiHat;
        this.dataService = dataService;
        this.userId = userId;
        this.listener = listener;
    }

    @NonNull
    @Override
    //Tạo giao diện (layout) cho một item trong danh sách
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_baihatyeuthich, parent, false);
        return new ViewHolder(view);
    }

    @Override
    //Gán dữ liệu thực tế cho từng dòng hiển thị:
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BaiHat baiHat = danhSachBaiHat.get(position);//Lấy dữ liệu bài hát tại vị trí position trong danh sách bài hát
        //Gán các dữ liệu vào các view con
        holder.tvTenBaiHat.setText(baiHat.getTenBaiHat());
        holder.tvTenCaSi.setText(baiHat.getCaSi());

        //thư viện Glide – một công cụ giúp tải ảnh từ Internet (hoặc file nội bộ) rồi hiển thị lên ImageView
        Glide.with(context)
                .load(baiHat.getHinhAnh())
                .placeholder(R.drawable.music_placeholder)
                .error(R.drawable.music_placeholder)
                .into(holder.imgBiaNhac);

        // Click icon tim
        holder.imgTimYeuThich.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                xoaKhoiYeuThich(baiHat.getId(), pos);
            }
        });

        // Click vào bài hát → gọi callback lên Activity
        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener != null) {
                listener.onSongClick(baiHat, pos);
            }
        });
    }


    private void xoaKhoiYeuThich(int idBaiHat, int position) {
        Call<Void> call = dataService.deleteFavorite(userId, idBaiHat);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    danhSachBaiHat.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, danhSachBaiHat.size());
                    Toast.makeText(context, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                    Log.e("API_DELETE", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối server!", Toast.LENGTH_SHORT).show();
                Log.e("API_DELETE", "Lỗi khi xóa bài hát", t);
            }
        });
    }

    @Override
    public int getItemCount() {
        return danhSachBaiHat != null ? danhSachBaiHat.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {//Giữ tham chiếu đến các View con bên trong mỗi item của RecyclerView
                                                                    //, giúp Adapter có thể truy cập và gán dữ liệu nhanh hơn, không cần gọi lại findViewById() nhiều lần.
        ImageView imgBiaNhac, imgTimYeuThich;
        TextView tvTenBaiHat, tvTenCaSi;

        public ViewHolder(@NonNull View itemView) {//đại diện cho một dòng (item) trong danh sách RecyclerView.
                                                    //lưu các view con (TextView, ImageView…) để hiển thị thông tin của bài hát.
            super(itemView);
            imgBiaNhac = itemView.findViewById(R.id.imgBiaNhac);
            imgTimYeuThich = itemView.findViewById(R.id.imgTimYeuThich);
            tvTenBaiHat = itemView.findViewById(R.id.tvTenBaiHat);
            tvTenCaSi = itemView.findViewById(R.id.tvTenCaSi);
        }
    }
}