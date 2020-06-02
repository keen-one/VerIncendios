package softnecessary.viewfires;

import androidx.annotation.NonNull;

public final class Incendio {

  private double latitud = 0.0;
  private double longitud = 0.0;
  private String direccion = "";


  private String fecha = "";
  private String tiempo = "";
  private int recursoBandera = 0;

  final String getFecha() {
    return fecha;
  }

  final void setFecha(String fecha) {
    this.fecha = fecha;
  }

  final String getTiempo() {
    return tiempo;
  }

  final void setTiempo(String tiempo) {
    this.tiempo = tiempo;
  }

  @NonNull
  @Override
  public String toString() {
    return "Incendio{" +
        "latitud=" + latitud +
        ", longitud=" + longitud +
        ", direccion='" + direccion + '\'' +
        ", fecha='" + fecha + '\'' +
        ", tiempo='" + tiempo + '\'' +
        ", recursoBandera=" + recursoBandera +
        '}';
  }


  final int getRecursoBandera() {
    return recursoBandera;
  }

  final void setRecursoBandera(int recursoBandera) {
    this.recursoBandera = recursoBandera;
  }

  final double getLatitud() {
    return latitud;
  }

  void setLatitud(double latitud) {
    this.latitud = latitud;
  }

  final double getLongitud() {
    return longitud;
  }

  final void setLongitud(double longitud) {
    this.longitud = longitud;
  }

  final String getDireccion() {
    return direccion;
  }

  final void setDireccion(String direccion) {
    this.direccion = direccion;
  }


}
