package com.bailcompany.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.bailcompany.R;
import com.bailcompany.model.DefendantModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

/**
 * Created by admin on 1/30/2018.
 */

public class CustomInfoWindowDefendantAdapter implements GoogleMap.InfoWindowAdapter {
    View myContentsView;

    Context mContext;
    ArrayList<DefendantModel> defList;
    public CustomInfoWindowDefendantAdapter(Context context, ArrayList<DefendantModel> defList) {
        this.mContext = context;
        myContentsView= LayoutInflater.from(context).inflate(R.layout.dialog_defendant_details, null);
        this.defList=defList;
    }
    private void renderWindowText(Marker marker, View view){
        String defId=marker.getSnippet();
        DefendantModel defDetail=getDefendantDetailById(defId);

    }

    private DefendantModel getDefendantDetailById(String defId) {

        if (defId == null)
            return null;
        DefendantModel defDetail = null;
        for (DefendantModel d : defList) {
            if (d.getId() != null && d.getId().equalsIgnoreCase(defId)) {
                defDetail = d;
                break;

            }

        }
        return defDetail;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker,myContentsView);
        return myContentsView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker,myContentsView);
        return myContentsView;
    }
}
