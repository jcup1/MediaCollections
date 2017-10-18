package mywins.theandroiddev.com.mediacollections;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jakub on 17.10.17.
 */

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioAdapterViewHolder> {

    private static OnItemClickListener mListener;
    private List<Audio> audioList;

    public AudioAdapter(List<Audio> audioList) {
        this.audioList = audioList;
        setHasStableIds(true);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public long getItemId(int position) {
        return audioList.get(position).getId();
    }

    @Override
    public AudioAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_item, parent, false);
        return new AudioAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(AudioAdapterViewHolder holder, int position) {
        String title = position + 1 + "    " + audioList.get(position).getTitle();
        holder.audioTitle.setText(title);
        holder.itemView.setTag(audioList.get(position));

    }

    @Override
    public int getItemCount() {
        return audioList == null ? 0 : audioList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    static class AudioAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView audioTitle;


        public AudioAdapterViewHolder(View view) {
            super(view);
            this.audioTitle = view.findViewById(R.id.audio_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) mListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
