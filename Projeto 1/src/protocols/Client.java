package protocols;

import java.io.IOException;

public class Client {

	public static void main(String args[]) throws IOException,
			NumberFormatException, InterruptedException {

		Config config = new Config(args);

		if (!config.readConfigurations() && args.length != 6) {
			System.out
					.println("java Server <MC> <MC_port> <MDB> <MDB_port> <MDR> <MDR_port>");
			System.exit(-1);
		} else if (args.length == 6)
			config.storeConfigurations();

		startSubProtocols(config);
	}

	private static void startSubProtocols(Config config) {
		Thread ChunkBackup = new Thread(new Runnable() {

			ChunkBackup chunkBackup = new ChunkBackup(config);

			@Override
			public void run() {
				// quando a thread começa tem que estar pronto para enviar
				// pedidos e para receber pedidos logo terá que se juntar a um
				// servidor de multicast para ficar a espera de pedidos, sempre
				// que se pretende criar um backup é criado é criado um
				// datagramsocket para o efeito que depois é fechado!
				
				//duvida? criar apenas 1 receiver que recebe todos os pedidos e os interpreta ou separar logo por threads??
				chunkBackup.start();
			}

		});
		ChunkBackup.start();

		Thread fileRestore = new Thread(new Runnable() {

			FileRestore fileRestore = new FileRestore(config);

			@Override
			public void run() {
				fileRestore.start();
			}

		});
		fileRestore.start();
	}
}
