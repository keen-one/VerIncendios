package softnecessary.viewfires;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.security.SecureRandom;
import java.util.ArrayList;


public class AdapterIncendios extends RecyclerView.Adapter<AdapterIncendios.ViewHolder> {

  private ArrayList<Incendio> myItems;
  private ItemListener myListener;
  AdapterIncendios(ArrayList<Incendio> items, ItemListener listener) {
    myItems = items;
    myListener = listener;
  }


  @Override
  public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
  }


  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_rv, parent, false)); // TODO
  }

  @Override
  public int getItemCount() {
    return myItems.size();
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    //holder.setData(myItems.get(position));
    Incendio item = myItems.get(position);
    holder.bindIncendio(item);
    holder.tvDireccion.setText(item.getDireccion());
    holder.tvFecha.setText(item.getFecha());
    holder.tvTiempo.setText(item.getTiempo());
    if (item.getRecursoBandera() != 0) {
      holder.tvTiempo.setCompoundDrawablesWithIntrinsicBounds(0, 0, item.getRecursoBandera(), 0);
    } else {
      holder.tvTiempo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }
    int[] listRecurso = new int[]{R.drawable.fire_icons_1, R.drawable.fire_icons_2,
        R.drawable.fire_icons_3, R.drawable.fire_icons_4};
    SecureRandom random = new SecureRandom();
    int indexRecurso = random.nextInt(listRecurso.length);
    holder.imgFuego.setImageResource(listRecurso[indexRecurso]);

  }

  public interface ItemListener {

    void onItemClick(Incendio item);
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView tvDireccion;
    // TODO - Your view members
    private Incendio item;
    private TextView tvFecha;
    private TextView tvTiempo;
    private ImageView imgFuego;


    ViewHolder(View itemView) {
      super(itemView);
      itemView.setOnClickListener(this);
      tvDireccion = itemView.findViewById(R.id.tv_direccion);
      tvFecha = itemView.findViewById(R.id.tv_fecha);
      tvTiempo = itemView.findViewById(R.id.tv_tiempo);
      imgFuego = itemView.findViewById(R.id.img_fuego);


    }


    @Override
    public void onClick(View v) {
      if (myListener != null) {
        myListener.onItemClick(item);

      }
    }

    void bindIncendio(Incendio incendio) {
      item = incendio;


    }
  }


}
                                