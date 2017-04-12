package cz.tul.lp.testapp;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.improvelectronics.sync.obex.OBEXFtpFolderListingItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LP on 11.04.2017.
 */

class FileBrowserAdapter extends BaseAdapter {

    private List<OBEXFtpFolderListingItem> mFolderListingItems;
    private final Context context;

    public FileBrowserAdapter(Context context, ArrayList<OBEXFtpFolderListingItem> folderListingItems) {
        this.mFolderListingItems = folderListingItems;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (mFolderListingItems == null) return 0;
        else return mFolderListingItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mFolderListingItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mFolderListingItems.get(position).getTime().getTime();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parentView) {

        FileBrowserViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.list_item_browse_board, null);

            viewHolder = new FileBrowserViewHolder();
            viewHolder.mNameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            viewHolder.mDateTextView = (TextView) convertView.findViewById(R.id.dateTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (FileBrowserViewHolder) convertView.getTag();
        }

        OBEXFtpFolderListingItem bluetoothFtpFolderListingItem = mFolderListingItems.get(position);
        if (bluetoothFtpFolderListingItem != null) {
            // Set up the name of the bluetoothFtpFolderListingItem.
            viewHolder.mNameTextView.setText(bluetoothFtpFolderListingItem.getName());
            // Set up the icon of the bluetoothFtpFolderListingItem.
            if (bluetoothFtpFolderListingItem.getSize() > 0) {
                viewHolder.mDateTextView.setVisibility(View.VISIBLE);
                viewHolder.mDateTextView.setText(DateUtils.getRelativeTimeSpanString(bluetoothFtpFolderListingItem.getTime().getTime()
                        , System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE));
            } else {
                viewHolder.mDateTextView.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    private static class FileBrowserViewHolder {
        TextView mNameTextView;
        TextView mDateTextView;
    }

    public void setFolderListingItems(List<OBEXFtpFolderListingItem> bluetoothFtpFolderListingItems) {
        this.mFolderListingItems = bluetoothFtpFolderListingItems;
        notifyDataSetChanged();
    }
}