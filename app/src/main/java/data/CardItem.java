package data;

import java.io.Serializable;

public class CardItem implements Serializable {

    private String id;

    private String cid;

    private String deepLink;

    private String icon_url;

    private String icon_name;

    private String rank;

    private String url;

    public CardItem() {
    }

    public String getDeepLink() {
        return deepLink;
    }

    public void setDeepLink(String deepLink) {
        this.deepLink = deepLink;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getIcon_name() {
        return icon_name;
    }

    public void setIcon_name(String icon_name) {
        this.icon_name = icon_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "CardItem{" +
                "id='" + id + '\'' +
                ", cid='" + cid + '\'' +
                ", deepLink='" + deepLink + '\'' +
                ", icon_url='" + icon_url + '\'' +
                ", icon_name='" + icon_name + '\'' +
                ", rank='" + rank + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
