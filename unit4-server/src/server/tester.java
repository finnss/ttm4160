package server;

class tester 
{
  public static void main(String args[]) 
{ 
    //System.out.println("Hello, World"); 
    Thread th;
    Runnable r; 

    r = new Runnable(){
        @Override
        public void run(){
            new UdpClient().start();
        }
    };
    
    th = new Thread(r);
    th.start();
//    th.join();

} 
  
}