import java.net.*;
import java.util.*;
import java.io.*;
import java.security.*; 

import moses.member.*;
import moses.security.*;
import moses.controlState.*;
import moses.util.*;


public class SomeAgent implements Agent{

    public static int counter = 0;
    public static int last = 0;

    public void run (String[] args) throws Exception { 

	System.out.println("> java SomeAgent contrname contrport lawfile agname");
	System.out.println("> java SomeAgent contrname contrport lawfile agname CApub");
	System.out.println("> java SomeAgent contrname contrport lawfile agname CApub Mycert Myprivkey");
	
	//variables to read from the command line
	String contrname;
	String contrport;
	String lawfile;
	String agname;
	String ca = null;
	String certf = null;
	String priv = null;
	Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                        try
                        {       FileWriter writer = new FileWriter("packet_counter.csv",true);
                                writer.append("ma1");
                                writer.append(',');
                                writer.append("" + (SomeAgent.counter - SomeAgent.last));
                                writer.append('\n');
                                writer.flush();
                                writer.close();
                        }
                        catch(IOException e)
                        {
                                e.printStackTrace();
                        }
                        SomeAgent.last = SomeAgent.counter;
                }
        }, 1000, 1000);


	if(args.length < 4) {
	    System.out.println("> java SomeAgent contrname contrport lawfile agname");
	    System.out.println("> java SomeAgent contrname contrport lawfile agname CApub");
	    System.out.println("> java SomeAgent contrname contrport lawfile agname CApub Mycert Myprivkey");
	    return;
	}
	
	 contrname = args[0];
	 contrport = args[1];
	 lawfile = args[2];
	 agname = args[3];

	if(args.length ==5)
	    ca = args[4];
	
	if(args.length ==7) {
	    ca = args[4];
	    certf = args[5];
	    priv = args[6];
	}
	




	Member m;
	PublicKey pubca = null;
	if(ca != null) {
	    pubca =   certCreation.getPublicKey(ca);
	}
	

	FileInputStream fis  = new FileInputStream(lawfile);
        byte[] blaw = new byte[fis.available()];
        fis.read(blaw);
        String slaw = new String(blaw);



	
	if(pubca == null)    
	    m = new Member(slaw,Const.IMM_LAW,contrname, Integer.parseInt(contrport), agname);
	else
	    m = new Member(slaw,Const.IMM_LAW,contrname, Integer.parseInt(contrport), agname, pubca);


	if (certf!=null) {
	    
	    LGICert  cert = certCreation.getCert(certf);
	    PrivateKey privk =   certCreation.getPrivateKey(priv);
	    byte[] sign = null;
	    sign =  Secu.signSelfCertificate(cert, privk);

	    m.addCertificate(cert, sign);
	}

	System.out.println(m.adopt("mypassword","someargument"));

	//the agent should be ready to receive messages in here


	new Receiver(this,m).start();
	System.out.println("Type exit to quit");
	

	for(;;) {
	    
	    try{

		BufferedReader in
		    = new BufferedReader(new InputStreamReader(System.in));
            
		String command  = in.readLine();
	    
		if (command.equals("exit")) {
		    m.close();
		    System.out.println("The Agent is shutting down...");
		    System.exit(0);

		}

		if (command.startsWith("send")) {
		    Term ct = Term.parse(command);
		    if(ct == null) {
			System.out.println("send(msg,dest) -- not understood. Try again!");
			continue;
		    }

		    Term tt = Term.parse("send(%M,%D)");
		    UnifyResult ur = ct.unify(tt);
		    if(ur == null) {
			System.out.println("send(msg,dest) -- not understood. Try again!");
			continue;
		    }
		    String msg = ur.getSVar("M");
		    String dest = ur.getSVar("D");
		
		    m.send_lg(msg,dest);

		}
		if(command.startsWith("sss")){
   			for(int i = 0; i < 1000; i++){
				m.send_lg("test","s@controller2");
				this.counter++;
				Thread.sleep(200);
			}
                }        
	    } catch(Exception e) {
		;
	    }
	}
    
    }
    

    public void processRequest(Member member, String message, String destination) {
	;
    }
    
    public void processReply(Member member, String reply) {
	System.out.println("Received: " + reply);
    }
    
    public void processReply(Member member, byte[] breply) {
	System.out.println("Received: " + breply);
    }
    
    public void processReply(Member member, Object oreply) {
	System.out.println("Received: " + oreply);
    }
    

    


    public static void  main (String[] args) throws Exception { 

	SomeAgent somea = new SomeAgent();
	somea.run(args);
    }
      
}
