public class Email {

    private String from;
    private String to;
    private String title;
    private String body;

    Email(String to, String title, String body) {
        this.from = "";
        this.to = to;
        this.title = title;
        this.body = body;
    }

    Email() {
        this.from = "";
        this.to = "";
        this.title = "";
        this.body = "";
    }


    public void setFrom(String from) {
        this.from = from;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getBody() {
        return body;
    }

    public String getFrom() {
        return from;
    }

    public String getTitle() {
        return title;
    }

    public String getTo() {
        return to;
    }

    private String reprBodyWithTabs(){
        String formattedBody = "\t" + body.replaceAll("\n","\n\t");
        return formattedBody;
    }

    @Override
    public String toString(){
        return "From:\t" + from + "\nTo:\t\t" + to + "\nTitle:\t" + title + "\nBody:\n" + reprBodyWithTabs();
    }
}
