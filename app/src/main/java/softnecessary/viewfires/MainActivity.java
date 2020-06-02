package softnecessary.viewfires;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import softnecessary.viewfires.AdapterIncendios.ItemListener;

public class MainActivity extends AppCompatActivity {


  private Gson gson = new Gson();
  private ProgressBar progressBar;
  private TextView tvProgreso;
  private ItemListener listener;
  private ImageView img;
  private RecyclerView rv;
  private Handler handler = new Handler();

  private Runnable runnable = new Runnable() {
    @Override
    public void run() {
      try {
        if (!CapturaIncendio.isRunning) {

          new CapturaIncendio(MainActivity.this, progressBar, tvProgreso,
              img, rv, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
      } catch (Exception e) {
        e.printStackTrace();
        CapturaIncendio.isRunning = false;
      } finally {
        CapturaIncendio.isRunning = false;
        handler.postDelayed(runnable, 1000L * 60L * 10L);
      }


    }
  };


  @Override
  protected void onStart() {
    super.onStart();

  }

  @Override
  protected void onDestroy() {

    super.onDestroy();
  }

  @Override
  protected void onPause() {

    super.onPause();
  }


  public final void comenzarServicio(View view) {
    runnable.run();
  }

  public final void pararServicio(View view) {
    handler.removeCallbacks(runnable);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    rv = findViewById(R.id.my_rv);

    img = findViewById(R.id.img_lanzador);
    progressBar = findViewById(R.id.progressBar);

    tvProgreso = findViewById(R.id.tv_progreso);

    LinearLayoutManager linear = new LinearLayoutManager(this);
    rv.setLayoutManager(linear);
    rv.setHasFixedSize(true);
    listener = new ItemListener() {
      @Override
      public void onItemClick(Incendio item) {
        Intent intent = new Intent(MainActivity.this, DetalleIncendio.class);
        String json = gson.toJson(item, Incendio.class);
        intent.putExtra("DATA", json);
        startActivity(intent);
      }
    };
    AdapterIncendios adapter = new AdapterIncendios(App.listaIncendios, listener);
    rv.setAdapter(adapter);


  }

  @Override
  protected void onResume() {
    super.onResume();

  }
}
