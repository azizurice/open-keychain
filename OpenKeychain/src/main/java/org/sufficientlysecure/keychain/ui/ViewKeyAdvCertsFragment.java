/*
 * Copyright (C) 2014-2015 Dominik Schürmann <dominik@dominikschuermann.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sufficientlysecure.keychain.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import com.tonicartos.superslim.LayoutManager;

import org.sufficientlysecure.keychain.Constants;
import org.sufficientlysecure.keychain.R;
import org.sufficientlysecure.keychain.provider.KeychainContract;
import org.sufficientlysecure.keychain.ui.adapter.CertSectionedListAdapter;
import org.sufficientlysecure.keychain.ui.util.recyclerview.RecyclerFragment;
import org.sufficientlysecure.keychain.util.Log;

public class ViewKeyAdvCertsFragment extends RecyclerFragment<CertSectionedListAdapter>
        implements LoaderManager.LoaderCallbacks<Cursor>, CertSectionedListAdapter.CertListListener {

    public static final String ARG_DATA_URI = "data_uri";
    private Uri mDataUriCerts;

    /**
     * Creates new instance of this fragment
     */
    public static ViewKeyAdvCertsFragment newInstance(Uri dataUri) {
        ViewKeyAdvCertsFragment frag = new ViewKeyAdvCertsFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_DATA_URI, dataUri);

        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_key_adv_certs_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        hideList(false);

        Uri dataUri = getArguments().getParcelable(ARG_DATA_URI);
        if (dataUri == null) {
            Log.e(Constants.TAG, "Data missing. Should be Uri of key!");
            getActivity().finish();
            return;
        } else {
            mDataUriCerts = KeychainContract.Certs.buildCertsUri(dataUri);
        }

        CertSectionedListAdapter adapter = new CertSectionedListAdapter(getActivity(), null);
        adapter.setCertListListener(this);

        setAdapter(adapter);
        setLayoutManager(new LayoutManager(getActivity()));

        getLoaderManager().initLoader(0, null, this);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(getActivity(), mDataUriCerts,
                CertSectionedListAdapter.CertCursor.CERTS_PROJECTION, null, null,
                CertSectionedListAdapter.CertCursor.CERTS_SORT_ORDER);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Avoid NullPointerExceptions, if we get an empty result set.
        if (data.getCount() == 0) {
            return;
        }

        // Swap the new cursor in. (The framework will take care of closing the
        // old cursor once we return.)
        getAdapter().swapCursor(CertSectionedListAdapter.CertCursor.wrap(data));

        if (isResumed()) {
            showList(true);
        } else {
            showList(false);
        }
    }

    /**
     * This is called when the last Cursor provided to onLoadFinished() above is about to be closed.
     * We need to make sure we are no longer using it.
     */
    public void onLoaderReset(Loader<Cursor> loader) {
        getAdapter().swapCursor(null);
    }

    @Override
    public void onClick(long masterKeyId, long signerKeyId, long rank) {
        if(masterKeyId != 0L) {
            Intent viewIntent = new Intent(getActivity(), ViewCertActivity.class);
            viewIntent.setData(KeychainContract.Certs.buildCertsSpecificUri(
                    masterKeyId, rank, signerKeyId));
            startActivity(viewIntent);
        }
    }
//<<<<<<< HEAD
//
//
//    /**
//     * Implements StickyListHeadersAdapter from library
//     */
//    private class CertListAdapter extends CursorAdapter implements StickyListHeadersAdapter {
//        private LayoutInflater mInflater;
//        private int mIndexMasterKeyId, mIndexUserId, mIndexRank;
//        private int mIndexSignerKeyId, mIndexSignerUserId;
//        private int mIndexVerified, mIndexType;
//
//        public CertListAdapter(Context context, Cursor c) {
//            super(context, c, 0);
//
//            mInflater = LayoutInflater.from(context);
//            initIndex(c);
//        }
//
//        @Override
//        public Cursor swapCursor(Cursor newCursor) {
//            initIndex(newCursor);
//
//            return super.swapCursor(newCursor);
//        }
//
//        /**
//         * Get column indexes for performance reasons just once in constructor and swapCursor. For a
//         * performance comparison see http://stackoverflow.com/a/17999582
//         *
//         * @param cursor
//         */
//        private void initIndex(Cursor cursor) {
//            if (cursor != null) {
//                mIndexMasterKeyId = cursor.getColumnIndexOrThrow(KeychainContract.Certs.MASTER_KEY_ID);
//                mIndexUserId = cursor.getColumnIndexOrThrow(KeychainContract.Certs.USER_ID);
//                mIndexRank = cursor.getColumnIndexOrThrow(KeychainContract.Certs.RANK);
//                mIndexType = cursor.getColumnIndexOrThrow(KeychainContract.Certs.TYPE);
//                mIndexVerified = cursor.getColumnIndexOrThrow(KeychainContract.Certs.VERIFIED);
//                mIndexSignerKeyId = cursor.getColumnIndexOrThrow(KeychainContract.Certs.KEY_ID_CERTIFIER);
//                mIndexSignerUserId = cursor.getColumnIndexOrThrow(KeychainContract.Certs.SIGNER_UID);
//            }
//        }
//
//        /**
//         * Bind cursor data to the item list view
//         * <p/>
//         * NOTE: CursorAdapter already implements the ViewHolder pattern in its getView() method.
//         * Thus no ViewHolder is required here.
//         */
//        @Override
//        public void bindView(View view, Context context, Cursor cursor) {
//
//            // set name and stuff, common to both key types
//            TextView wSignerKeyId = (TextView) view.findViewById(R.id.signerKeyId);
//            TextView wSignerName = (TextView) view.findViewById(R.id.signerName);
//            TextView wSignStatus = (TextView) view.findViewById(R.id.signStatus);
//
//            String signerKeyId = KeyFormattingUtils.beautifyKeyIdWithPrefix(
//                    cursor.getLong(mIndexSignerKeyId));
//            OpenPgpUtils.UserId userId = KeyRing.splitUserId(cursor.getString(mIndexSignerUserId));
//            if (userId.name != null) {
//                wSignerName.setText(userId.name);
//            } else {
//                wSignerName.setText(R.string.user_id_no_name);
//            }
//            wSignerKeyId.setText(signerKeyId);
//
//            switch (cursor.getInt(mIndexType)) {
//                case WrappedSignature.DEFAULT_CERTIFICATION: // 0x10
//                    wSignStatus.setText(R.string.cert_default);
//                    break;
//                case WrappedSignature.NO_CERTIFICATION: // 0x11
//                    wSignStatus.setText(R.string.cert_none);
//                    break;
//                case WrappedSignature.CASUAL_CERTIFICATION: // 0x12
//                    wSignStatus.setText(R.string.cert_casual);
//                    break;
//                case WrappedSignature.POSITIVE_CERTIFICATION: // 0x13
//                    wSignStatus.setText(R.string.cert_positive);
//                    break;
//                case WrappedSignature.CERTIFICATION_REVOCATION: // 0x30
//                    wSignStatus.setText(R.string.cert_revoke);
//                    break;
//            }
//
//
//            view.setTag(R.id.tag_mki, cursor.getLong(mIndexMasterKeyId));
//            view.setTag(R.id.tag_rank, cursor.getLong(mIndexRank));
//            view.setTag(R.id.tag_certifierId, cursor.getLong(mIndexSignerKeyId));
//        }
//
//        @Override
//        public View newView(Context context, Cursor cursor, ViewGroup parent) {
//            return mInflater.inflate(R.layout.view_key_adv_certs_item, parent, false);
//        }
//
//        /**
//         * Creates a new header view and binds the section headers to it. It uses the ViewHolder
//         * pattern. Most functionality is similar to getView() from Android's CursorAdapter.
//         * <p/>
//         * NOTE: The variables mDataValid and mCursor are available due to the super class
//         * CursorAdapter.
//         */
//        @Override
//        public View getHeaderView(int position, View convertView, ViewGroup parent) {
//            HeaderViewHolder holder;
//            if (convertView == null) {
//                holder = new HeaderViewHolder();
//                convertView = mInflater.inflate(R.layout.view_key_adv_certs_header, parent, false);
//                holder.text = (TextView) convertView.findViewById(R.id.stickylist_header_text);
//                holder.count = (TextView) convertView.findViewById(R.id.certs_num);
//                convertView.setTag(holder);
//            } else {
//                holder = (HeaderViewHolder) convertView.getTag();
//            }
//
//            if (!mDataValid) {
//                // no data available at this point
//                Log.d(Constants.TAG, "getHeaderView: No data available at this point!");
//                return convertView;
//            }
//
//            if (!mCursor.moveToPosition(position)) {
//                throw new IllegalStateException("couldn't move cursor to position " + position);
//            }
//
//            // set header text as first char in user id
//            String userId = mCursor.getString(mIndexUserId);
//            holder.text.setText(userId);
//            holder.count.setVisibility(View.GONE);
//            return convertView;
//        }
//
//        /**
//         * Header IDs should be static, position=1 should always return the same Id that is.
//         */
//        @Override
//        public long getHeaderId(int position) {
//            if (!mDataValid) {
//                // no data available at this point
//                Log.d(Constants.TAG, "getHeaderView: No data available at this point!");
//                return -1;
//            }
//
//            if (!mCursor.moveToPosition(position)) {
//                throw new IllegalStateException("couldn't move cursor to position " + position);
//            }
//
//            // otherwise, return the first character of the name as ID
//            return mCursor.getInt(mIndexRank);
//
//            // sort by the first four characters (should be enough I guess?)
//            // return ByteBuffer.wrap(userId.getBytes()).asLongBuffer().get(0);
//        }
//
//        class HeaderViewHolder {
//            TextView text;
//            TextView count;
//        }
//
//    }
//=======
//>>>>>>> cc6f9037948f8d3e5481c7479b1ce5e607e9a01f
}
