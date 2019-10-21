/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidordoslang;

/**
 *
 * @author oscar
 */
public class File {
    private String name;
    private String content;
    private boolean main;

    public File(String name, String content, boolean main) {
        this.name = name;
        this.content = content;
        this.main = main;
    }

    
    public File() {
        name = "";
        content = "";
        main = false;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the main
     */
    public boolean isMain() {
        return main;
    }

    /**
     * @param main the main to set
     */
    public void setMain(boolean main) {
        this.main = main;
    }
    
}
