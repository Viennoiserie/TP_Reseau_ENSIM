import java.net.*;
import java.util.Arrays;

public class UDP_server_multi extends Thread{

    static long client_sum[] = new long [100];
    static long client_port[] = new long [10000];
    
    static long i; // Je veux être sur que les clients qui arrivent soient reconnus
    static long result;

    static int PORT = 5000;
    static int one_client_log;
    static int one_client_PORT;
    static SocketAddress one_client_addr;

    public static void main(String[] args){

        Arrays.fill(client_sum, 0);    // Initialisation de toutes les sommes à 0
        Arrays.fill(client_port, 5000); // Initialisation de tous les ports à 5000
        
        UDP_server_multi serveur = new UDP_server_multi();

        try {

            DatagramSocket socket = new DatagramSocket(PORT);

            while(true) {

                byte[] data_id = new byte[8];
                DatagramPacket paquet_id = new DatagramPacket(data_id, data_id.length);
                
                socket.receive(paquet_id); //Reception de l'id du client
                i = ByteUtils.bytesToLong(paquet_id.getData());

                System.out.println("\nNous avons recu le nombre " + i + "\n");
                System.out.println("Vérifions si un client s'appelle déjà comme ca . . .");

                if(client_port[(int)i] == 5000) { // Je vérifies si j'ai déjà un client

                    System.out.println("\n- - - NOUS AVONS UN NOUVEAU CLIENT : LE NUMERO " + i + " - - - \n");

                    client_port[(int)i] = 5000 + i; // Si un emplacement est libre : je crée un nouveau port
                    one_client_log = (int)i;
                    one_client_addr = paquet_id.getSocketAddress(); // J'enregistre mes variables pour les passer dans les threads

                    Thread one_client = new Thread(serveur);

                    byte[] data_PORT = ByteUtils.longToBytes(client_port[(int)i]);
                    DatagramPacket paquet_PORT = new DatagramPacket(data_PORT, data_PORT.length, paquet_id.getSocketAddress());

                    socket.send(paquet_PORT);
                    one_client.start();        
                }

                else {

                    System.out.println("\nNous avons déjà un client numero " + i + " . . .");
                    System.out.println("Prevenons l'utilisateur qu'il ne peut pas se connecter . . . \n");
                    long error = PORT + 101;

                    byte[] data_error = ByteUtils.longToBytes(error);

                    DatagramPacket paquet_PORT = new DatagramPacket(data_error, data_error.length, paquet_id.getSocketAddress());
                    socket.send(paquet_PORT);
                }

            }

        } catch (Exception ex) { 
            
        ex.printStackTrace(); 
        System.exit(1);

        }
    }

	public void run() {

		try {	// Code équivalent au monoclient : à partir du tableau des clients j'incremente leur sommes

            int log_client = (int)client_port[one_client_log]; 
            SocketAddress actual_addr = one_client_addr;
            DatagramSocket socket_one_client = new DatagramSocket(log_client);
            System.out.println(log_client);
            byte[] data_sum = new byte[8];
			DatagramPacket paquet_sum = new DatagramPacket(data_sum, data_sum.length);	

			do {

                result = 0;
                socket_one_client.receive(paquet_sum); 
				result  = ByteUtils.bytesToLong(paquet_sum.getData());
                System.out.println("Il nous a envoyé un " + result);

				client_sum[(int)i] += result;
				System.out.println("Cela nous donne donc une somme de " + client_sum[(int)i] + "\n");

			} while(result != 0);
			
			byte [] data_somme = ByteUtils.longToBytes(client_sum[(int)i]);
			DatagramPacket paquet_to_send = new DatagramPacket(data_somme, data_somme.length, actual_addr);

			socket_one_client.send(paquet_to_send); //Envoi de la réponse vers le bon client
			System.out.println("Envoi confirmé ! \n");
			
		} catch (Exception ex) {

			ex.printStackTrace();
			System.exit(1);
		}
	}
}
