/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.model;

public class Menu {
    private String menuId;
    private String name;
    private int price;
    private String category;
    private boolean isAvailable;
    
    public Menu(String menuid, String name, int price, String category) {
        this.menuId = menuid;
        this.name = name;
        this.price = price;
        this.category = category;
        this.isAvailable = false;
    }
    
    public String getMenuId() {
        return menuId;
    }
    public String getName() {
        return name;
    }
    public int getPrice() {
        return price;
    }
    public String getCategory() {
        return category;
    }
    public boolean getIsAvailable() {
        return isAvailable;
    }
}
