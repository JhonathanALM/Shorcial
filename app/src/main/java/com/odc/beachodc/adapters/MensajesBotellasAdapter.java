package com.odc.beachodc.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.odc.beachodc.R;
import com.odc.beachodc.db.models.MensajeBotella;

import com.odc.beachodc.utilities.AnimateFirstDisplayListener;
import com.odc.beachodc.utilities.Utilities;

import java.util.List;

/**
 * Created by Paco on 09/07/2014.
 */
public class MensajesBotellasAdapter extends BaseAdapter {

    protected Activity activity;
    protected List<MensajeBotella> items;
    ViewHolder viewHolder;

    public MensajesBotellasAdapter(Activity activity, List<MensajeBotella> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return (items == null) ? 0 : items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.item_mensajes_botellas_list, null);
            viewHolder.nombreautor = (TextView) vi.findViewById(R.id.nombreAutorTV);
            viewHolder.fecha = (TextView) vi.findViewById(R.id.fechaTV);
            viewHolder.mensaje = (TextView) vi.findViewById(R.id.mensajeBotellaTV);
            viewHolder.origen = (TextView) vi.findViewById(R.id.playaOrigenTV);
            viewHolder.profilePictureView = (ProfilePictureView) vi.findViewById(R.id.fotoAutorImage);
            vi.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) vi.getTag();
        }

        // Trabajar con la UI, que es 'vi'

        MensajeBotella mensajeBotella = items.get(position);

        viewHolder.nombreautor.setText(mensajeBotella.nombreautor);
        viewHolder.fecha.setText(Utilities.formatFecha(mensajeBotella.fecha));
        viewHolder.mensaje.setText(mensajeBotella.mensaje);

        viewHolder.origen.setText(mensajeBotella.nombreplayadestino);

        viewHolder.profilePictureView.setCropped(true);
        viewHolder.profilePictureView.setProfileId(mensajeBotella.idfbautor);

        return vi;
    }

    static class ViewHolder {
        TextView nombreautor;
        TextView fecha;
        TextView mensaje;
        TextView origen;
        ProfilePictureView profilePictureView;
    }

}
