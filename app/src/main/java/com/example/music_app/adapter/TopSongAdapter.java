package com.example.music_app.adapter;

import android.content.Context;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.music_app.R;
import com.example.music_app.model.BaiHat;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class TopSongAdapter extends RecyclerView.Adapter<TopSongAdapter.ViewHolder> {

    Context context;
    ArrayList<BaiHat> list;

    public TopSongAdapter(Context context, ArrayList<BaiHat> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_top_song, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BaiHat song = list.get(position);
        holder.tvSongName.setText(song.getTenBaiHat());
        holder.tvSinger.setText(song.getCaSi());
        Picasso.get().load(song.getHinhAnh()).into(holder.imgSong);

        holder.imgHeart.setOnClickListener(v ->
                Toast.makeText(context, "Đã thêm vào yêu thích ❤️", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSong, imgHeart;
        TextView tvSongName, tvSinger;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSong = itemView.findViewById(R.id.imgSong);
            imgHeart = itemView.findViewById(R.id.imgHeart);
            tvSongName = itemView.findViewById(R.id.tvSongName);
            tvSinger = itemView.findViewById(R.id.tvSinger);
        }
    }
}
