import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;

public class Img_process extends Frame {

	int i, j, k;
	int ch = 0;
	Image ImageObj;
	int inTop, inLeft;
	int size = 512;
	int image[][] = new int[size][size];

	int sum = 0;

	public Img_process() {
	}

	public Img_process(int n) {
		size = n;
		image = new int[size][size];
	}

	public static void main(String[] args) throws IOException {
		int cnt = 0;
		ArrayList<Img_process> obj = new ArrayList<Img_process>();

		obj.add(new Img_process());
		obj.get(cnt).readimg("Lenna512.raw");
		obj.get(cnt).reduction(300);
		obj.get(cnt).output("Lenna512_s.raw");
		cnt++;

		obj.add(new Img_process(300));
		obj.get(cnt).readimg(obj.get(0).image);
		obj.get(cnt).mirror_v();
		obj.get(cnt).cw();
		obj.get(cnt).blur();
		obj.get(cnt).output("Lenna512_s_mh.raw");
		cnt++;

		obj.add(new Img_process(300));
		obj.get(cnt).readimg(obj.get(0).image);
		obj.get(cnt).mirror_h();
		obj.get(cnt).cw();
		obj.get(cnt).cw();
		obj.get(cnt).sharp();
		obj.get(cnt).sharp();
		obj.get(cnt).output("Lenna512_s_mv.raw");
		cnt++;

		obj.add(new Img_process());
		obj.get(cnt).readimg("Lenna512.raw");
		obj.get(cnt).bigduction(600);
		obj.get(cnt).output("Lenna512_b.raw");

		obj.get(cnt).cover_img(obj.get(0), 0, 0);
		obj.get(cnt).cover_img(obj.get(1), 0, 300);
		obj.get(cnt).cover_img(obj.get(2), 300, 0);
		obj.get(cnt).output("Lenna512_b.raw");

		obj.get(cnt).show_img();

	} //main

	public void readimg(String input_image) throws IOException {
		InputStream fis = new FileInputStream(input_image); 
		for (int i = 0; i < size; i++) 
			for (int j = 0; j < size; j++)
				if ((ch = fis.read()) != -1) {
					image[i][j] = ch;
				}
	}
	
	public void readimg(int a[][]) {
		image = a;
	}

	public void cw() throws IOException {
		int ch;
		int a[][] = new int[size][size]; 
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) { 
				a[i][j] = image[size - j - 1][i]; 
			}
		}
		image = a;
	}

	public void mirror_h() throws IOException {
		int ch;
		int a[][] = new int[size][size]; 
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) { 
				a[i][j] = image[i][size - j - 1];
			}
		}
		image = a;
	}

	public void mirror_v() throws IOException {
		int ch;
		int a[][] = new int[size][size]; 
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) { 
				a[i][j] = image[size - i - 1][j]; 
			}
		}
		image = a;
	}

	public void output(String output_image) throws IOException {
		System.out.println("輸出圖檔：  " + output_image + ".");

		OutputStream fout = new FileOutputStream(output_image); 
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) { 
				fout.write(image[i][j]);
			}
		}
	}

	void sharp() throws IOException {
		int addvalue = 0, divisor = 4;
		int mask[][] = { // Sharpping Filter Mask
				{ 0, 0, 0, 0, 0 }, // 將中心點的重要性提高8倍
				{ 0, 0, -1, 0, 0 }, // 相鄰的點設為-1
				{ 0, -1, 8, -1, 0 }, // 相離的點設為0
				{ 0, 0, -1, 0, 0 }, { 0, 0, 0, 0, 0 } };

		for (int third = 0; third < 1; third++) {
			for (int i = 2; i < size-3; i++) { // 計算5x5區塊內mask之影響
				for (int j = 2; j < size-3; j++) {
					sum = 0;
					for (int k = -2; k <= 2; k++) {// 5x5區塊內mask之計算
						for (int l = -2; l <= 2; l++) {
							sum += image[i + k][j + l] * (mask[2 + k][2 + l]);

						}
					}
					image[i][j] = (int) (((sum * 3) / (divisor * 3)) + addvalue);// 銳化處理
					if (image[i][j] < 0)
						image[i][j] = 0;
					if (image[i][j] > 255)
						image[i][j] = 255;
				}
			}
		}
	}
	
	void blur() throws IOException {
		int ch, i, j, k, l, t, sum = 0;
		
		int addvalue = 2, divisor = 25;
		int mask[][] = { // Blurring Filter Mask
				{ 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1 }, 
				{ 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1 }, 
				{ 1, 1, 1, 1, 1 } };
		for (i = 2; i < size-3; i++) { // 計算5x5區塊內mask之影響
			for (j = 2; j < size-3; j++) {
				sum = 0;
				for (k = -2; k <= 2; k++) { // 5x5區塊內mask之計算
					for (l = -2; l <= 2; l++) { // 5x5區塊內mask之計算
						sum += image[i + k][j + l] * mask[2 + k][2 + l];
					}

					image[i][j] = (int) ((sum / divisor) + addvalue);
				}
			}
		}
		
	}// blur

	void reduction(int output_size) throws IOException {
		int x, y;
		int[][] Z = new int[output_size][output_size];

		for (i = 0; i < size; i++) // scan origin write to smaller img
			for (j = 0; j < size; j++) {
				x = (output_size * i) / size;
				y = (output_size * j) / size;
				Z[x][y] = image[i][j];
			}

		image = new int[output_size][output_size];
		image = Z;
		// for (i = 0; i < output_size; i++)// write
		// for (j = 0; j < output_size; j++){
		// image[i][j] = Z[i][j];
		// System.out.println(" " + image[i][j] + " ");
		// }
		size = output_size;
	}

	void bigduction(int output_size) throws IOException {
		int x, y;
		int[][] Z = new int[output_size][output_size];

		for (i = 0; i < output_size; i++) 
			for (j = 0; j < output_size; j++) {
				x = (size * i) / output_size;
				y = (size * j) / output_size;
				Z[i][j] = image[x][y];
			}
		image = new int[output_size][output_size];
		image = Z;
		size = output_size;
	}

	public void cover_img(Img_process a, int istart, int jstart) {
		for (i = 0; i < a.size; i++) {
			for (j = 0; j < a.size; j++) {
				image[i + istart][j + jstart] = a.image[i][j];
			}
		}
	}

	public void show_img() throws IOException {

		// MemoryImageSource只收一維陣列
		int p41[] = new int[size * size];
		int k=0;
		for (i = 0; i < size ; i++) {
			for (j=0; j<size; j++, k++){
				p41[k] = image[i][j] | image[i][j] << 8 | image[i][j] << 16 | 0XFF000000; // LRGB
			}
		}
		ImageObj = createImage(new MemoryImageSource(size, size, p41, 0, size));
		setVisible(true);
		inTop = getInsets().top;
		inLeft = getInsets().left;
		setSize(inLeft + size, inTop + size);
		setTitle("Read Raw Image and Show Image on Frame");
		addWindowListener(new WindowAdapter() {
			public void WindowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	} // public void show_img() throws IOException

	public void paint(Graphics g) {
		if (ImageObj != null)
			g.drawImage(ImageObj, inLeft, inTop, this);
	}//public void paint(Graphics g)
}//public class Img_process extends Frame
