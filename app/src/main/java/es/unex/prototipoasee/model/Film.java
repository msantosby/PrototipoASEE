package es.unex.prototipoasee.model;



public class Film {
    private String fTitle;
    private int fImage;
    private String fDate;

    public Film(String title, int image, String date) {
        this.fImage = image;
        this.fTitle = title;
        this.fDate = date;
    }

    public int  getImage(){
        return this.fImage;
    }
    public String getTitle() {
        return this.fTitle;
    }
    public String getDate(){return this.fDate;}

    public void  setImage(int image){
        this.fImage = image;
    }
    public void setTitle(String title) {
        this.fTitle = title;
    }
    public void setDate(String date){this.fDate = date;}

}
