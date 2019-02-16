package com.gxchain.client.graphenej.models;

import com.gxchain.client.graphenej.Price;
import com.gxchain.client.graphenej.objects.GrapheneObject;

/**
 * This is the representation of the response from the 'get_objects' call with
 * a 2.4.x id, which will retrieve a 'impl_asset_bitasset_data_type'.
 *
 * Created by nelson on 1/8/17.
 */
public class BitAssetData extends GrapheneObject {
    public Object[] feeds;
    public AssetFeed current_feed;
    public String current_feed_publication_time;
    public Object options;
    public long force_settled_volume;
    public boolean is_prediction_market;
    public Price settlement_price;
    public long settlement_fund;

    public BitAssetData(String id) {
        super(id);
    }
}
