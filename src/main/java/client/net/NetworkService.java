package client.net;

import java.io.*;
import java.net.*;
/**
 *
 * @author user
 */
public class NetworkService {
    
    private static NetworkService instance;
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 5000;
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private NetworkService(){}
    
    public static NetworkService getInstance(){
        if (instance == null){
            instance = new NetworkService();
        }
        return instance;
    }
    
    public void connect(){ //서버 접속 시도
        try{
            socket = new Socket(SERVER_IP, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            
            System.out.println("서버 연결 성공");
        }
        catch(IOException ex){
            System.out.println("서버 연결 실패");
            ex.printStackTrace();
        }
    }
    
    public String sendRequest(String msg){
        if(socket == null || socket.isClosed()){
            return "오류";
        }
        
        try{
            out.println(msg); //서버로 전송
            
            String response = in.readLine(); //서버 응답 대기
            return response;
        }
        
        catch(IOException ex){
            ex.printStackTrace();
            return "오류";
        }
    }
    
    public void disconnect(){ // 프로그램 종료 시 소켓 닫음
        try{
            if(socket != null)
                socket.close();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }
}
