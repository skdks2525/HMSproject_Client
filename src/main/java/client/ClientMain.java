/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package client;
import client.net.NetworkService;
import client.view.LoginFrame;
/**
 *
 * @author user
 */
public class ClientMain {
    
    public static void main(String[] args) {
        System.out.println("클라이언트 프로그램 실행");
        
        NetworkService.getInstance().connect(); // 서버 연결 시도
        new LoginFrame();
        
        //String response = NetworkService.getInstance().sendRequest("LOGIN:adin:admin123");
    }
}
