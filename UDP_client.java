import java.util.Scanner;
import java.net.*;

public class UDP_client{
	public static void main(String[] args) {
		try {

			int PORT;
            int error = 0;

			long nb;
            long id;

			DatagramSocket socket = new DatagramSocket();
			
			System.out.println("\nVeuillez entrer le PORT du serveur : ");

			Scanner scanner = new Scanner(System.in);
			PORT = scanner.nextInt();

			InetSocketAddress addr = new InetSocketAddress("localhost", PORT);	
            
            System.out.println("\nQui êtes vous ? ");
			id = scanner.nextLong(); //Client saisi l'id et le port du serveur

            byte[] data_id = ByteUtils.longToBytes(id);
            DatagramPacket paquet_for_id = new DatagramPacket(data_id, data_id.length, addr);	
			socket.send(paquet_for_id); // J'envoie mon id au serveur

            byte[] data_of_PORT = new byte[8];
            DatagramPacket paquet_of_PORT = new DatagramPacket(data_of_PORT, data_of_PORT.length);
            // Je récupère la réponse du serveur pour savoir ou je serai redirigé

            socket.receive(paquet_of_PORT);
            PORT = (int)ByteUtils.bytesToLong(paquet_of_PORT.getData());

            System.out.println("\n - - - ATTENDEZ LA VALIDITE DE CONNEXION - - - \n");
            System.out.println("Le serveur vous attribue le PORT : " + PORT + "\n");

            if(PORT < 5101) // Si le serveur me donne un port normal je continue 
            {
                addr = new InetSocketAddress("localhost", PORT);
            }
            else // Sinon je fais en sorte de ne pas entrer dans la boucle de la somme de chiffres
            {
                System.out.println("\n Vous ne pouvez pas vous connecter !\n");
                socket.close();
                error = 1;
            }

			do {	
                
                if(error == 1) { break; }

				System.out.println("\nSaisissez un long à ajouter à la somme : ");
				nb = scanner.nextLong();

                //Convertion des données en tableaux de bytes
                byte[] data_sum = ByteUtils.longToBytes(nb);

                DatagramPacket paquet_for_sum = new DatagramPacket(data_sum, data_sum.length, addr);	
				socket.send(paquet_for_sum); // Envoi d'un nombre pour alimenter la somme

			} while(nb != 0);
			
            if(error == 0) { // Si je n'ai pas d'erreur -

                byte [] data_res = new byte[8];
                DatagramPacket paquet_to_rcv = new DatagramPacket(data_res, data_res.length);
                
                System.out.println("\n- - - En attente de resultat - - -");
                socket.receive(paquet_to_rcv); //Reception de la réponse d'un des threads du serveur
                
                long res  = ByteUtils.bytesToLong(paquet_to_rcv.getData());
                System.out.println("\n Le resultat de la somme est : " + res + "\n");
            }

            socket.close();
            scanner.close();
						
		} catch (Exception ex) {

            ex.printStackTrace();
            System.exit(1);
		}
	} 
}
