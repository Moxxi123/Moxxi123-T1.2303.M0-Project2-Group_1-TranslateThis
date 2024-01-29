package Control;


public class Word {
    int id;
    String word, pronounce, type, definition, update, updateddefinition;
    

    // Hàm dựng
    public Word() {
    }

    public Word(int id, String word, String pronounce, String type, String definition, String update, String updateddefinition) {
        this.id = id;
        this.word = word;
        this.pronounce = pronounce;
        this.type = type;
        this.definition = definition;
        this.update = update;
        this.updateddefinition = updateddefinition;
    }

    public int getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public String getPronounce() {
        return pronounce;
    }

    public String getType() {
        return type;
    }

    public String getDefinition() {
        return definition;
    }

    public String getUpdate() {
        return update;
    }

    public String getUpdateddefinition() {
        return updateddefinition;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public void setUpdateddefinition(String updateddefinition) {
        this.updateddefinition = updateddefinition;
    } 
  
}
