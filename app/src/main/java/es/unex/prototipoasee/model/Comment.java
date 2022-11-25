package es.unex.prototipoasee.model;

public class Comment {
    private String cUsername;
    private String cText;

    public Comment(String username, String text) {
        this.cUsername = username;
        this.cText = text;
    }

    public String  getUsername(){
        return this.cUsername;
    }
    public String getText() {
        return this.cText;
    }

    public void  setUsername(String username){
        this.cUsername = username;
    }
    public void setText(String text) {
        this.cText = text;
    }
}
