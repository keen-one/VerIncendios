package softnecessary.viewfires.pojo;

import androidx.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "cksum",
    "downloadsLink",
    "kind",
    "md5sum",
    "mtime",
    "name",
    "self",
    "size"
})
public final class MainYaml {

  @JsonProperty("cksum")
  private Object cksum = new Object();
  @JsonProperty("downloadsLink")
  private String downloadsLink = "";
  @JsonProperty("kind")
  private String kind = "";
  @JsonProperty("md5sum")
  private Object md5sum = new Object();
  @JsonProperty("mtime")
  private Integer mtime = 0;
  @JsonProperty("name")
  private String name = "";
  @JsonProperty("self")
  private String self = "";
  @JsonProperty("size")
  private Integer size = 0;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<>();

  @JsonProperty("cksum")
  public final Object getCksum() {
    return cksum;
  }

  @JsonProperty("cksum")
  public final void setCksum(Object cksum) {
    this.cksum = cksum;
  }

  @JsonProperty("downloadsLink")
  public final String getDownloadsLink() {
    return downloadsLink;
  }

  @JsonProperty("downloadsLink")
  public final void setDownloadsLink(String downloadsLink) {
    this.downloadsLink = downloadsLink;
  }

  @JsonProperty("kind")
  public final String getKind() {
    return kind;
  }

  @JsonProperty("kind")
  public final void setKind(String kind) {
    this.kind = kind;
  }

  @JsonProperty("md5sum")
  public final Object getMd5sum() {
    return md5sum;
  }

  @JsonProperty("md5sum")
  public final void setMd5sum(Object md5sum) {
    this.md5sum = md5sum;
  }

  @JsonProperty("mtime")
  public final Integer getMtime() {
    return mtime;
  }

  @JsonProperty("mtime")
  public final void setMtime(Integer mtime) {
    this.mtime = mtime;
  }

  @JsonProperty("name")
  public final String getName() {
    return name;
  }

  @JsonProperty("name")
  public final void setName(String name) {
    this.name = name;
  }

  @JsonProperty("self")
  public final String getSelf() {
    return self;
  }

  @JsonProperty("self")
  public final void setSelf(String self) {
    this.self = self;
  }

  @JsonProperty("size")
  public final Integer getSize() {
    return size;
  }

  @JsonProperty("size")
  public final void setSize(Integer size) {
    this.size = size;
  }

  @JsonAnyGetter
  public final Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public final void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

  @NonNull
  @Override
  public String toString() {
    return "MainYaml{" +
        "cksum=" + cksum +
        ", downloadsLink='" + downloadsLink + '\'' +
        ", kind='" + kind + '\'' +
        ", md5sum=" + md5sum +
        ", mtime=" + mtime +
        ", name='" + name + '\'' +
        ", self='" + self + '\'' +
        ", size=" + size +
        ", additionalProperties=" + additionalProperties +
        '}';
  }
}
