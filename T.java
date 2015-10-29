/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016;

/**
 *
 * @author alexhuleatt
 */
public class T {
    private final Object[] mem;

    public static T $(Object...mem) {
        return new T(mem);
    }
    
    public <K> K get(int index) {return (K)mem[index];}
    
    private T(Object[] mem) {this.mem=mem;}
    
    public int len() {return mem.length;}    
    public Class type(int index) {return mem[index].getClass();}
}
