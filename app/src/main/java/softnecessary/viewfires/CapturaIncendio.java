package softnecessary.viewfires;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Stream;
import javax.net.ssl.HttpsURLConnection;
import softnecessary.viewfires.pojo.MainYaml;

public class CapturaIncendio extends AsyncTask<Void, Integer, ArrayList<Incendio>> {

  private static final String myUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:74.0) Gecko/20100101 Firefox/74.0";
  static boolean isRunning = false;
  private final String DATO_LONG = "DATO_LONG";
  private boolean esChile = false;
  private Locale local = new Locale("es", "CL");
  /*private NameValuePair autorizacion = new NameValuePair() {
    @Override
    public String getName() {
      return "Authorization";
    }

    @Override
    public String getValue() {
      return "Bearer " + App.APP_KEY;
    }
  };

  private NameValuePair agent = new NameValuePair() {
    @Override
    public String getName() {
      return "User-Agent";
    }

    @Override
    public String getValue() {
      return myUserAgent;
    }
  };*/
  private Handler handler = new Handler();
  private WeakReference<Context> weakContext;
  private WeakReference<ProgressBar> weakProgressBar;
  private WeakReference<TextView> weakTvProgreso;
  private WeakReference<ImageView> weakImg;
  private AdapterIncendios.ItemListener listener;
  private WeakReference<RecyclerView> weakRV;
  private int newDato = -1;
  private Runnable runnable = new Runnable() {
    @Override
    public void run() {
      try {

        realizarCambios(0, 25);

        operacionBackground();
        realizarCambios(50, 99);

      } catch (Exception e) {
        e.printStackTrace();
      } finally {

        handler.postDelayed(runnable, 1000L * 60L * 12L);
      }


    }
  };

  CapturaIncendio(Context context, ProgressBar progressBar, TextView tvProgreso,
      ImageView img, RecyclerView miRV, AdapterIncendios.ItemListener listener) {
    ThreadPolicy policy = new ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
    weakContext = new WeakReference<>(context);
    weakProgressBar = new WeakReference<>(progressBar);
    weakTvProgreso = new WeakReference<>(tvProgreso);
    weakImg = new WeakReference<>(img);
    weakRV = new WeakReference<>(miRV);
    this.listener = listener;
  }

  private void guardarDato(int numero) {
    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(weakContext.get());
    pref.edit().putInt(DATO_LONG, numero).apply();
  }

  private int loadDato() {
    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(weakContext.get());
    return pref.getInt(DATO_LONG, 0);
  }

  @Override
  protected ArrayList<Incendio> doInBackground(Void... voids) {
    isRunning = true;
    realizarCambios(0, 25);
    ArrayList<Incendio> miArray = operacionBackground();
    realizarCambios(50, 99);
    return miArray;
  }

  @Override
  protected void onPreExecute() {
    isRunning = true;
    weakProgressBar.get().setProgress(0);
    weakTvProgreso.get().setText(R.string._0_progreso);
  }

  private void realizarCambios(Integer min, Integer max) {
    for (Integer cont = min; cont <= max; cont++) {
      try {
        Thread.sleep(1000);
        publishProgress(cont);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void sendMessage(Integer numero) {

    if (numero >= 0 && numero <= 49) {
      weakTvProgreso.get()
          .setText(String.valueOf(numero).concat("%").concat(" Preparando datos..."));
    }
    if (numero >= 50 && numero <= 99) {
      weakTvProgreso.get()
          .setText(String.valueOf(numero).concat("%").concat(" Almacenando datos..."));
    }
    weakProgressBar.get().setProgress(numero);

  }


  @Override
  protected void onPostExecute(ArrayList<Incendio> incendios) {

    if (incendios.size() > 0) {
      App.listaIncendios = new ArrayList<>(incendios);
      weakImg.get().setVisibility(View.GONE);
    }
    Comparator<Incendio> comparator = new Comparator<Incendio>() {
      @Override
      public int compare(Incendio o1, Incendio o2) {
        return o1.getDireccion().compareToIgnoreCase(o2.getDireccion());
      }
    };
    Collections.sort(App.listaIncendios, comparator);
    AdapterIncendios adapter = new AdapterIncendios(App.listaIncendios, listener);
    weakRV.get().setAdapter(adapter);
    weakTvProgreso.get().setText(R.string.completado);
    weakProgressBar.get().setProgress(100);
    isRunning = false;
  }

  @Override
  protected void onProgressUpdate(Integer... values) {
    Integer valor = values[0];
    sendMessage(valor);
  }

  /*
    @Override
    public void onCreate() {
      super.onCreate();
      startForeground(14, createNotification());
    }

    public Notification createNotification() {
      String channelID = "INCENDIOALERT";
      NotificationChannel channel = new NotificationChannel(channelID, channelID,
          NotificationManager.IMPORTANCE_LOW);
      channel.setDescription(getString(R.string.app_name));
      NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
      if (manager != null) {
        manager.createNotificationChannel(channel);
      }

      NotificationCompat.Builder builder = new Builder(this, channelID);
      builder.setDefaults(Notification.DEFAULT_ALL);
      return builder.build();
    }
    */
  private ArrayList<Incendio> operacionBackground() {

    ArrayList<MainYaml> list = getYalm();

    Collections.sort(list, new Comparator<MainYaml>() {
      @Override
      public int compare(MainYaml o1, MainYaml o2) {
        if (o1.getMtime() > o2.getMtime()) {
          return -1;
        }
        if (o1.getMtime().equals(o2.getMtime())) {
          return 0;
        }

        return 1;


      }
    });

    if (list.size() > 0) {
      MainYaml obj = list.get(0);
      if (obj != null) {

        Integer newDato1 = obj.getMtime();
        if (newDato1 == null) {
          newDato = -1;
        } else {
          newDato = newDato1;
        }

        String url = obj.getDownloadsLink().trim();
        String urlBase = "https://nrt3.modaps.eosdis.nasa.gov";
        String urlGeneral = urlBase.concat(url);

        realizarCambios(25, 50);

        getListIncendiosV2(urlGeneral);


      }
    }

    return App.listaIncendios;

  }


  private boolean tieneIncendio(Incendio incendioNew) {
    for (Incendio incendioOld : App.listaIncendios) {
      if (incendioOld.getDireccion().equalsIgnoreCase(incendioNew.getDireccion())) {
        return false;
      }
    }
    return true;
  }
  /*
  @Override
  public void onDestroy() {
    super.onDestroy();

    stopForeground(true);
  }*/

  private Incendio stringToIncendio(String cadena) {
    String[] datos = cadena.split(",");
    String latitud;
    String longitud;
    String fecha;
    String tiempo;
    try {
      latitud = datos[0].trim();
      longitud = datos[1].trim();
      fecha = datos[5].trim();
      tiempo = datos[6].trim();
    } catch (Exception e) {
      latitud = "";
      longitud = "";
      fecha = "";
      tiempo = "";
    }

    double latitud1 = stringToDouble(latitud);
    double longitud1 = stringToDouble(longitud);
    Calendar c = getFecha(fecha.concat(" " + tiempo));
    Incendio incendio = new Incendio();
    incendio.setLatitud(latitud1);
    incendio.setLongitud(longitud1);
    incendio.setRecursoBandera(esChile ? R.drawable.ic_chile : 0);
    if (c != null) {
      SimpleDateFormat sdfFecha = new SimpleDateFormat("dd-MM-yyyy", local);
      SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm", local);
      incendio.setFecha(sdfFecha.format(c.getTime()));
      incendio.setTiempo(sdfHora.format(c.getTime()));
    }
    String pais = getCountry(latitud1, longitud1);
    if (pais != null) {
      if (pais.equalsIgnoreCase("chile")) {
        String direccion = getDireccion(latitud1, longitud1);
        incendio.setDireccion(direccion);
      }
    }

    return incendio;
  }

  private String getDireccion(double latitud, double longitud) {
    Geocoder geocoder = new Geocoder(weakContext.get(), local);
    try {
      List<Address> address = geocoder.getFromLocation(latitud, longitud, 1);
      if (address.size() > 0) {
        String direccion = address.get(0).getAddressLine(0);
        if (direccion == null) {
          return "";
        } else {
          return direccion;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "";
  }

  private String getCountry(double latitud, double longitud) {
    Geocoder geocoder = new Geocoder(weakContext.get(), local);
    try {
      List<Address> direcciones = geocoder.getFromLocation(latitud, longitud, 1);
      return direcciones.get(0).getCountryName();
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }

  }

  private Calendar getFecha(String dateTime) {
    SimpleDateFormat sdfOld = new SimpleDateFormat("yyyy-MM-dd HH:mm", local);
    sdfOld.setTimeZone(TimeZone.getTimeZone("UTC"));
    SimpleDateFormat sdfNew = new SimpleDateFormat("dd-MM-yyyy HH:mm", local);
    sdfNew.setTimeZone(TimeZone.getTimeZone("America/Santiago"));

    Calendar calendar = Calendar.getInstance(local);
    calendar.setTimeZone(TimeZone.getTimeZone("America/Santiago"));
    esChile = true;
    try {
      Date dateOld = sdfOld.parse(dateTime);
      if (dateOld != null) {
        String stringDateNew = sdfNew.format(dateOld);
        Date dateNew = sdfNew.parse(stringDateNew);
        if (dateNew != null) {
          calendar.setTime(dateNew);

        }

        //calendar.setTime(dateOld);
      }

    } catch (ParseException e) {
      calendar = Calendar.getInstance(local);
      esChile = false;
      calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
      try {
        Date dateOld1 = sdfOld.parse(dateTime);
        if (dateOld1 != null) {
          calendar.setTime(dateOld1);

        }

      } catch (ParseException ex) {
        return null;
      }
    }
    return calendar;
  }

  private double stringToDouble(String cadena) {

    try {
      return Double.parseDouble(cadena);
    } catch (Exception e) {
      return 0.0;
    }

  }

  private void getListIncendiosV2(String url) {
    File file = new File(weakContext.get().getExternalCacheDir(), "data_incendios.txt");
    int dato = loadDato();
    if (dato != newDato) {
      descargarTxt(url, file);
    } else {
      if (file.exists()) {
        convertLines(file);
      }

    }

  }

  private void descargarTxt(String url, File file) {
    try {
      InputStream inStream = getResource(url);
      if (inStream != null) {

        copyInputStreamToFile(inStream, file);
        if (file.exists() && file.getTotalSpace() > 1) {
          convertLines(file);
          guardarDato(newDato);
        }

      }


    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private void convertLines(File result) {
    try {
      Stream<String> lineas = Files.lines(result.toPath());

      lineas.forEach(s -> hacerTrabajo(s));

    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  private void hacerTrabajo(String line) {
    Incendio incendio = stringToIncendio(line);
    if (!incendio.getDireccion().equals("")) {
      if (tieneIncendio(incendio)) {
        App.listaIncendios.add(incendio);
      }

    }
  }

  private InputStream getResource(
      String resource) throws Exception {
    URL url = new URL(resource);
    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setInstanceFollowRedirects(false);
    connection.setUseCaches(false);
    connection.setDoInput(true);

    //connection.setDoOutput(true);
    connection.setRequestProperty(
        "Authorization",
        "Bearer " + App.APP_KEY);
    connection.setRequestProperty("User-Agent", myUserAgent);

    int status = connection.getResponseCode();

    if (status == 200) {
      return connection.getInputStream();
    } else {
      return null;
    }


  }

  private String obtenerLineas(String resource) {
    String resultado = "";

    try {
      /*
       * Set up a cookie handler to maintain session cookies. A custom
       * CookiePolicy could be used to limit cookies to just the resource
       * server and URS.
       */
      CookieManager cookieManger = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
      CookieHandler.setDefault(cookieManger);

      /* Retreve a stream for the resource */
      InputStream in = getResource(resource);
      Log.d("hola", "");
      if (in != null) {
        Log.d("hola", "entro aqui");

        /* Dump the resource out (not a good idea for binary data) */
        InputStreamReader input = new InputStreamReader(in);
        BufferedReader bin = new BufferedReader(
            input);
        String line;
        while ((line = bin.readLine()) != null) {
          resultado = resultado.concat(line + "\n");
        }
        bin.close();
      }
      return resultado;
    } catch (Exception ignore) {

    }
    return "";
  }

  private void copyInputStreamToFile(InputStream in, File file) {
    OutputStream out = null;

    try {
      out = new FileOutputStream(file);
      byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // Ensure that the InputStreams are closed even if there's an exception.
      try {
        if (out != null) {
          out.close();
        }

        // If you want to close the "in" InputStream yourself then remove this
        // from here but ensure that you close it yourself eventually.
        in.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private ArrayList<MainYaml> getYalm() {
    ArrayList<MainYaml> listYaml = new ArrayList<>();
    try {
      String urlJSON = "https://nrt3.modaps.eosdis.nasa.gov/api/v2/content/details/FIRMS/viirs/South_America?fields=all&format=json";
      String yaml = obtenerLineas(urlJSON);
      if (!yaml.equals("")) {

        char[] arrayChar = yaml.toCharArray();
        int inicio = 0;
        int fin;
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        for (int i = 0; i < arrayChar.length; i++) {
          char letra = arrayChar[i];
          if (letra == '{') {
            inicio = i;
          }
          if (letra == '}') {
            fin = i;

            String yamlSmall = yaml.substring(inicio, fin + 1);
            yamlSmall = yamlSmall.replaceAll("\\\\", "");
            MainYaml obj = mapper.readValue(yamlSmall, MainYaml.class);
            if (!listYaml.contains(obj)) {
              listYaml.add(obj);
            }
          }
        }

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return listYaml;
  }


  /*@Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    runnable.run();
    startForeground(14, createNotification());
    return START_STICKY_COMPATIBILITY;
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }*/
}
