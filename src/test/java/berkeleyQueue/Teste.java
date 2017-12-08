package berkeleyQueue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import br.com.easydoc.berkeleyDBLib.dao.BerkeleyDAO;
import br.com.easydoc.berkeleyDBLib.queue.BerkeleyQueue;

public class Teste {

	public static final int max = 3;
	
	//@Test
	public void teste() {
		try {
			List<BerkeleyQueue<Data>> list = new ArrayList<BerkeleyQueue<Data>>();
			for (int i=0; i<max; i++) {
				String name = "fila" + i;
				BerkeleyQueue<Data> queue = new BerkeleyQueue<>("~/queueTest", name, Data.class);
				list.add(queue);
			}
			for (int i=0; i<max; i++) {
				System.out.println(list.get(i).getName());
			}

			for (int i = 0; i < 20; i++) {
				new Thread() {
					public void run() {
						try {
							Data item = new Data("Example Text");
							Random rand = new Random();
							for (int r=0; r<1000; r++) {
								System.out.println("Iteração: " + r);
								int i = rand.nextInt() % max;
								i = Math.abs(i);
								if (rand.nextLong() % 2 == 0) {
									System.out.println("insere");
									item.setTimestamp(new Date().getTime());
									list.get(i).push(item);
									System.out.println("Inseriu um item na Fila " + i);
								} else {
									Data queue = list.get(i).pull();
									if (queue != null) {
										System.out.println(queue.getText());
										System.out.println("Tamanho da Fila: " + list.get(i).size());
									} else {
										System.out.println("Fila " + i + " está vazia");
									}
								}
							}
							System.out.println("saiu");
						}catch(Exception e) {
							e.printStackTrace();
							System.out.println("Eita");
						}
					}
				}.start();
			}
			Thread.sleep(20000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//@Test
	public void teste2() {
		Data data = new Data("OI");
		data.setId(1l);
		try {
			new BerkeleyDAO<Data, Long>("./data", Data.class).save(data);
			new BerkeleyDAO<Data, Long>("./data", Data.class).save(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
