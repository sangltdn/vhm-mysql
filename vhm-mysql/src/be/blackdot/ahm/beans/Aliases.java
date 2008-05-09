/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.blackdot.ahm.beans;

/**
 *
 * @author sjorge
 * @url http://www.blackdot.be
 */
public class Aliases {

    private int id;
    private String alias;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
    
    @Override
    public String toString(){
        return getAlias();
    }
}
