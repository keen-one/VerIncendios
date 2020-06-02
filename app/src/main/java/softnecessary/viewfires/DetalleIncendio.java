package softnecessary.viewfires;

import android.Manifest;
import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController.Visibility;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class DetalleIncendio extends AppCompatActivity {

  private static final int STORAGE_CODE = 13;
  private boolean bandera = false;
  private MapView mapView;
  private Snackbar snack;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
    setContentView(R.layout.activity_detalle_incendio);
    Intent intent = getIntent();
    String json = intent.getStringExtra("DATA");
    TextView tvLocalizacion = findViewById(R.id.tv_localizacion);
    TextView tvDireccion = findViewById(R.id.tv_direccion);
    RelativeLayout layout = findViewById(R.id.layout_direccion);
    int pxHeight = getResources().getDisplayMetrics().heightPixels;
    RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.MATCH_PARENT, (int) (pxHeight * 0.25));

    layout.setLayoutParams(layoutParam);
    OnClickListener listenerClick = new OnClickListener() {
      @Override
      public void onClick(View v) {
        ActivityCompat
            .requestPermissions(DetalleIncendio.this,
                new String[]{permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_CODE);
      }
    };
    snack = Snackbar.make(findViewById(android.R.id.content), "Permiso de almacenamiento",
        BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("PERMITIR", listenerClick);
    if (ActivityCompat.checkSelfPermission(DetalleIncendio.this, permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(DetalleIncendio.this, permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
      bandera = false;
      if (!snack.isShown()) {
        snack.show();
      }

    } else {
      bandera = true;
      if (snack.isShown()) {
        snack.dismiss();
      }
    }
    if (bandera) {

      mapView = findViewById(R.id.miMapa);
      mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

    }

    if (json != null) {
      Gson gson = new Gson();
      Incendio incendio = gson.fromJson(json, Incendio.class);
      if (incendio != null) {

        String direccion = incendio.getDireccion();
        double latitud = incendio.getLatitud();
        double longitud = incendio.getLongitud();
        if (direccion.isEmpty()) {
          tvDireccion.setText(R.string.direccion_desconocida);
        } else {
          tvDireccion.setText(direccion);
        }
        if (latitud == 0.0 && longitud == 0.0) {
          tvLocalizacion.setText(R.string.localizacion_desconocida);
        } else {
          String miLatitud = "Lat:".concat(String.valueOf(latitud));
          String miLongitud = " Lon:".concat(String.valueOf(longitud));
          tvLocalizacion.setText(miLatitud.concat(miLongitud));
          GeoPoint point = new GeoPoint(latitud, longitud);
          if (bandera) {
            if (mapView != null) {

              mapView.getController().setCenter(point);
              IMapController controller = mapView.getController();
              controller.setZoom(13.0);
              mapView.setMultiTouchControls(true);
              mapView.getZoomController().setVisibility(Visibility.NEVER);
              controller.setCenter(point);
              Marker marker = new Marker(mapView);
              marker.setPosition(point);
              Drawable drawable = getDrawable(R.drawable.ic_alert);
              marker.setIcon(drawable);
              marker.setImage(drawable);
              marker.setTitle(tvLocalizacion.getText().toString());
              if (mapView.getOverlays().size() > 0) {
                mapView.getOverlays().set(0, marker);
              } else {
                mapView.getOverlays().add(0, marker);
              }
            }

          }
        }

      }
    }

  }

  public final void prepararLLamada(View view) {
    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "132", null));
    startActivity(intent);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == STORAGE_CODE) {
      for (int i = 0; i < permissions.length; i++) {
        String permission = permissions[i];
        int grantResult = grantResults[i];

        if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
          if (grantResult == PackageManager.PERMISSION_GRANTED) {
            recreate();
          } else {
            if (!snack.isShown()) {
              snack.show();
            }
          }
        }
        if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
          if (grantResult == PackageManager.PERMISSION_GRANTED) {
            bandera = true;
            recreate();
          } else {
            bandera = false;
            if (!snack.isShown()) {
              snack.show();
            }
          }
        }
      }

    }
  }
}
