
package map;

import java.awt.*;

import java.awt.event.ActionEvent;

import java.util.*;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.*;
import java.util.List;
import javax.swing.Timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class Map extends JFrame {
  private int greenDroidVisibilityRange = 3; // Default visibility range for the green droid
private final Color transparentColor = new Color(255, 255, 255, 100); // Transparent white color
private final Color blackColor = Color.BLACK;
private boolean povGreenDroidActive = false; // Menandakan apakah pandangan droid hijau aktif

    private int[][] map;
    private final int numRows = 25;
    private final int numCols = 25; 
    private final int cellSize = 28;
    private final JPanel mapPanel;
    private final JButton generateButton;
    private final JButton RedButton;
    private final JButton GreenButton;
     private final JButton MulaiButton;
      private final JButton PauseButton;
     private final JButton acakhijauButton;
      private final JButton randomizeRedDroidsButton;
      private final JButton PovmerahButton;
      private final JButton PoverahButton; 
    //private final JButton MulaiButton;
    private final ArrayList<Point> redDroids = new ArrayList<>(); // variabel untuk menyimpan droid merah
    private Point greenDroid; // variabel untuk menyimpan droid hijau
    private final Random rand;
private ScheduledExecutorService executor;
  
    public Map() {
        setTitle("DROID MERAH KEJAR DROID HIJAU");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        map = new int[numRows][numCols];

        // membuat panel untuk peta
        mapPanel = new JPanel();
        mapPanel.setPreferredSize(new Dimension(numCols * cellSize, numRows * cellSize));
        mapPanel.setLayout(new GridLayout(numRows, numCols));
        getContentPane().add(mapPanel, BorderLayout.CENTER);

 
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(10,1));

        // membuat tombol untuk peta acak
        generateButton = new JButton("ACAK/RESET PETA");
        generateButton.addActionListener(e -> generateMap());
        buttonPanel.add(generateButton);

        // membuat tombol untuk peta acak
        RedButton = new JButton("tambah droid merah");
        RedButton.addActionListener(e -> placeDroid());
        buttonPanel.add(RedButton);
        
         // membuat tombol untuk peta acak
        PoverahButton = new JButton("pov droid merah");
        PoverahButton.addActionListener(e -> povDroidMerah());
        buttonPanel.add(PoverahButton);
        
        PovmerahButton = new JButton("pov droid hijau");
       PovmerahButton.addActionListener(e -> togglePovGreenDroid());
       buttonPanel.add(PovmerahButton);

        // membuat tombol untuk peta acak
        GreenButton = new JButton("tambah droid hijau");
        GreenButton.addActionListener(e -> placeGreenDroid());
        buttonPanel.add(GreenButton);
         // membuat tombol untuk memulai simulasi
        MulaiButton = new JButton("Mulai Simulasi");
        MulaiButton.addActionListener(e -> startSimulation());
        buttonPanel.add(MulaiButton);
        
         // membuat tombol untuk menghentikan simulasi
        PauseButton = new JButton("Pause");
        PauseButton.addActionListener(e -> stopSimulation());
        buttonPanel.add(PauseButton);
        
        JSlider visibilitySlider = new JSlider(JSlider.HORIZONTAL, 1, 25, greenDroidVisibilityRange);
visibilitySlider.addChangeListener(e -> {
    greenDroidVisibilityRange = visibilitySlider.getValue();
   povGreenDroid();
});
 buttonPanel.add(visibilitySlider);

        
        randomizeRedDroidsButton = new JButton("Acakdroidmerah");
        randomizeRedDroidsButton.addActionListener(e -> acakDroidMerah());
        buttonPanel.add(randomizeRedDroidsButton);
        
          acakhijauButton = new JButton("AcakdroiHijau");
        acakhijauButton.addActionListener(e ->   randomizeGreenDroid());
        buttonPanel.add(acakhijauButton);
        
      
        getContentPane().add(buttonPanel, BorderLayout.EAST);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        rand = new Random();
    }
    

// method untuk generate map acak
    private void generateMap() {
        // menghapus droid merah dan hijau yang telah ada
        for (Point droid : redDroids) {
            JPanel cell = (JPanel) mapPanel.getComponent(droid.x * numCols + droid.y);
            cell.removeAll();
        }
        redDroids.clear();
        if (greenDroid != null) {
            JPanel cell = (JPanel) mapPanel.getComponent(greenDroid.x * numCols + greenDroid.y);
            cell.removeAll();
            greenDroid = null;
        }

        map = new int[numRows][numCols]; // inisialisasi variabel map
    for (int i = 0; i < numRows; i++) {
        Arrays.fill(map[i], 1);
    }

        int startX = rand.nextInt(numRows);
        int startY = rand.nextInt(numCols);

        generatePath(startX, startY);

       // menggambar peta
      mapPanel.removeAll();
    for (int i = 0; i < numRows; i++) {
        for (int j = 0; j < numCols; j++) {
            JPanel cell = new JPanel();
            cell.setOpaque(true);
            cell.setPreferredSize(new Dimension(cellSize, cellSize));
            if (map[i][j] == 1) {
                cell.setBackground(Color.BLACK);
            } else {
                cell.setBackground(Color.WHITE);
                 cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            }
            mapPanel.add(cell);
        }
    }
    mapPanel.revalidate();
    mapPanel.repaint();
    
}



    
    
private void generatePath(int x, int y) {
    map[x][y] = 0; // buat jalan pada posisi ini

    // buat list semua arah yang bisa diambil
    List<int[]> directions = new ArrayList<>();
    directions.add(new int[]{-2, 0});
    directions.add(new int[]{0, -2});
    directions.add(new int[]{2, 0});
    directions.add(new int[]{0, 2});

    // acak urutan arah
    Collections.shuffle(directions);

    // coba setiap arah
    for (int[] direction : directions) {
        int newX = x + direction[0];
        int newY = y + direction[1];
        if (newX >= 0 && newX < numCols && newY >= 0 && newY < numRows && map[newX][newY] == 1) {
            int midX = x + direction[0] / 2;
            int midY = y + direction[1] / 2;
            map[midX][midY] = 0; // buat jalan pada posisi tengah

            // tambahkan kemungkinan untuk memilih arah lain
            if (0.4 > Math.random()) {
                // acak arah lain
                List<int[]> otherDirections = new ArrayList<>(directions);
                otherDirections.remove(direction);
                Collections.shuffle(otherDirections);

                // coba arah lain
                boolean pathFound = false;
                for (int[] otherDirection : otherDirections) {
                    int otherX = x + otherDirection[0];
                    int otherY = y + otherDirection[1];
                    if (otherX >= 0 && otherX < numCols && otherY >= 0 && otherY < numRows && map[otherX][otherY] == 1) {
                        int otherMidX = x + otherDirection[0] / 2;
                        int otherMidY = y + otherDirection[1] / 2;
                        map[otherMidX][otherMidY] = 0; // buat jalan pada posisi tengah

                        generatePath(otherX, otherY);
                        pathFound = true;
                        break;
                    }
                }

                // jika tidak ditemukan jalan pada arah lain, lanjutkan ke arah selanjutnya
                if (!pathFound) {
                }
            } else {
                generatePath(newX, newY);
            }
        }
    }
}
 private void acakDroidMerah() {
    // Mengecek apakah ada droid merah yang sudah ada di peta
    if (redDroids.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Tidak ada droid merah yang ada di peta.");
        return;
    }

    // Inisialisasi list randomRedDroids dengan posisi droid merah yang akan diacak
    ArrayList<Point> randomRedDroids = new ArrayList<>(redDroids);

    // Menghapus droid merah yang telah ada
    for (Point droid : redDroids) {
        JPanel cell = (JPanel) mapPanel.getComponent(droid.x * numCols + droid.y);
        cell.removeAll();
    }
    redDroids.clear();

    // Mengacak urutan droid merah
    Collections.shuffle(randomRedDroids);

    // Menempatkan ulang droid merah pada posisi yang diacak
    for (Point droid : randomRedDroids) {
        // Mencari sebuah sel yang merupakan jalan
        
        int x, y;
        do {
            x = rand.nextInt(numRows);
            y = rand.nextInt(numCols);
        } while (map[x][y] != 0);

        // Menempatkan droid pada sel tersebut
        JPanel cell = (JPanel) mapPanel.getComponent(x * numCols + y);
        cell.setLayout(new BorderLayout());
        JLabel redDroidLabel = new JLabel("●", SwingConstants.CENTER);
        redDroidLabel.setForeground(Color.red);
        Font droidFont = new Font("Arial", Font.BOLD, 40);
        redDroidLabel.setFont(droidFont);
        cell.add(redDroidLabel, BorderLayout.CENTER);
        redDroids.add(new Point(x, y));
    }

    mapPanel.revalidate();
    mapPanel.repaint();
}


private void moveRedDroid(Point redDroidPos) {
    Point current = redDroidPos;
    Point greenDroidPos = new Point(greenDroid.x, greenDroid.y);

    Queue<Point> queue = new LinkedList<>();
    queue.add(current);

    boolean[][] visited = new boolean[numRows][numCols];
    visited[current.x][current.y] = true;

    Point[][] parent = new Point[numRows][numCols];

    boolean foundPath = false;

    while (!queue.isEmpty()) {
        Point currentPoint = queue.poll();

        if (currentPoint.equals(greenDroidPos)) {
            foundPath = true;
            break;
        }

        List<Point> neighbors = getValidNeighbors(currentPoint);
        for (Point neighbor : neighbors) {
            if (!visited[neighbor.x][neighbor.y]) {
                queue.add(neighbor);
                visited[neighbor.x][neighbor.y] = true;
                parent[neighbor.x][neighbor.y] = currentPoint;
            }
        }
    }

    if (foundPath) {
        List<Point> path = new ArrayList<>();
        Point currentPos = greenDroidPos;

        while (!currentPos.equals(current)) {
            path.add(currentPos);
            currentPos = parent[currentPos.x][currentPos.y];
        }

        JPanel currentCell = (JPanel) mapPanel.getComponent(current.x * numCols + current.y);
        currentCell.removeAll();

        Point nextPos = path.get(path.size() - 1);
        JPanel newCell = (JPanel) mapPanel.getComponent(nextPos.x * numCols + nextPos.y);
        newCell.setLayout(new BorderLayout());
        JLabel droidLabel = new JLabel("●", SwingConstants.CENTER);
        droidLabel.setForeground(Color.RED);
        Font droidFont = new Font("Arial", Font.BOLD, 40);
        droidLabel.setFont(droidFont);
        newCell.add(droidLabel, BorderLayout.CENTER);

        redDroidPos.setLocation(nextPos);

        mapPanel.revalidate();
        mapPanel.repaint();

        if (nextPos.equals(greenDroidPos)) {
            executor.shutdownNow();
            JOptionPane.showMessageDialog(null, "Droid merah berhasil menangkap droid hijau!");
        }
    }
}




private List<Point> getValidNeighbors(Point current) {
    List<Point> neighbors = new ArrayList<>();
    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Atas, Bawah, Kiri, Kanan

    for (int[] dir : directions) {
        int newX = current.x + dir[0];
        int newY = current.y + dir[1];

        // Memeriksa apakah posisi baru berada pada jalan yang valid
        if (newX >= 0 && newX < numRows && newY >= 0 && newY < numCols && map[newX][newY] == 0) {
            neighbors.add(new Point(newX, newY));
        }
    }

    return neighbors;
}



private void startRedDroidMovement() {
    executor = Executors.newScheduledThreadPool(redDroids.size());
    for (int i = 0; i < redDroids.size(); i++) {
        Point redDroidPos = redDroids.get(i);
        executor.scheduleAtFixedRate(() -> moveRedDroid(redDroidPos), 0, 300, TimeUnit.MILLISECONDS);
    }
}

private void stopRedDroidMovement() {
    executor.shutdownNow();
    // Optionally, you can await termination of the executor
    try {
        executor.awaitTermination(1, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
    }
}

private void startSimulation() {
    startRedDroidMovement();
    startGreenDroidMovement();
}

private void moveGreenDroid() {
    if (greenDroid == null) {
        return;
    }

    // Mendapatkan posisi droid hijau
    int greenX = greenDroid.x;
    int greenY = greenDroid.y;

    // Mendapatkan tetangga yang valid
    List<Point> validNeighbors = getValidNeighbors(greenDroid);

    // Mengatur batasan jarak untuk bergerak
    double threshold = 8.0;

    //  menggunakan rumus Euclidean distance
    double minDistance = Double.MAX_VALUE;
    Point closestRedDroidPos = null;
    for (Point redDroidPos : redDroids) {
        double distance = Math.sqrt(Math.pow(redDroidPos.x - greenX, 2) + Math.pow(redDroidPos.y - greenY, 2));
        if (distance < minDistance) {
            minDistance = distance;
            closestRedDroidPos = redDroidPos;
        }
    }

    // Menggerakkan droid hijau berdasarkan jarak dengan droid merah terdekat
    if (minDistance < threshold) {
        // Memeriksa apakah droid hijau bersentuhan dengan droid merah terdekat
        if (greenX == closestRedDroidPos.x && greenY == closestRedDroidPos.y) {
            System.out.println("Droid Hijau telah tertangkap Droid Merah!");
            stopGreenDroidMovement();
            return; // Droid hijau berhenti bergerak
        }

        Point safeNeighbor = getSafeNeighbor(validNeighbors, closestRedDroidPos);

        if (safeNeighbor != null) {
            // Memindahkan droid hijau ke tetangga yang aman
            moveGreenDroidTo(safeNeighbor);
        }
        if (povGreenDroidActive) {
        povGreenDroid(); // Memanggil fungsi povGreenDroid() saat droid hijau bergerak
    }
    }
}


private Point getSafeNeighbor(List<Point> neighbors, Point redDroidPos) {
    double maxDistance = Double.MIN_VALUE;
    Point safeNeighbor = null;

    for (Point neighbor : neighbors) {
        int neighborX = neighbor.x;
        int neighborY = neighbor.y;

        // Memeriksa apakah tetangga adalah jalan atau tembok
        if (map[neighborX][neighborY] == 0) {
            boolean isSafe = true;
            for (Point redPos : redDroids) {
                // Memeriksa apakah tetangga berpotensi bersentuhan dengan droid merah lainnya
                if (redPos.x == neighborX && redPos.y == neighborY) {
                    isSafe = false;
                    break;
                }
            }

            if (isSafe) {
                int redX = redDroidPos.x;
                int redY = redDroidPos.y;

                double distance = Math.sqrt(Math.pow(redX - neighborX, 2) + Math.pow(redY - neighborY, 2));

                if (distance > maxDistance) {
                    maxDistance = distance;
                    safeNeighbor = neighbor;
                }
            }
        }
    }

    return safeNeighbor;
}


private void moveGreenDroidTo(Point newPosition) {
    // Menghapus droid hijau dari posisi saat ini
    JPanel currentCell = (JPanel) mapPanel.getComponent(greenDroid.x * numCols + greenDroid.y);
    currentCell.removeAll();

    // Menempatkan droid hijau pada posisi baru
    JPanel newCell = (JPanel) mapPanel.getComponent(newPosition.x * numCols + newPosition.y);
    newCell.setLayout(new BorderLayout());
    JLabel droid = new JLabel("●", SwingConstants.CENTER);
    droid.setForeground(Color.GREEN);
    Font droidFont = new Font("Arial", Font.BOLD, 40);
    droid.setFont(droidFont);
    newCell.add(droid, BorderLayout.CENTER);

    // Memperbarui posisi droid hijau pada variabel
    greenDroid.setLocation(newPosition);

    // Memperbarui tampilan peta
    mapPanel.revalidate();
    mapPanel.repaint();
    
        
}


private void togglePovGreenDroid() {
    povGreenDroidActive = !povGreenDroidActive; // Mengubah status pandangan droid hijau
  povGreenDroid(); // Memperbarui pandangan droid hijau sesuai dengan status yang baru
}


private void povGreenDroid() {
      if (greenDroid == null) {
        return;
    }

    int startX, startY, endX, endY;

    if (povGreenDroidActive) {
        startX = Math.max(0, greenDroid.x - greenDroidVisibilityRange);
        startY = Math.max(0, greenDroid.y - greenDroidVisibilityRange);
        endX = Math.min(numRows - 1, greenDroid.x + greenDroidVisibilityRange);
        endY = Math.min(numCols - 1, greenDroid.y + greenDroidVisibilityRange);
    } else {
        startX = 0;
        startY = 0;
        endX = numRows - 1;
        endY = numCols - 1;
    }

    for (int i = 0; i < numRows; i++) {
        for (int j = 0; j < numCols; j++) {
            JPanel cell = (JPanel) mapPanel.getComponent(i * numCols + j);
            if (cell != null) {
                if (i >= startX && i <= endX && j >= startY && j <= endY) {
                    if (map[i][j] == 0) {
                        cell.setBackground(transparentColor);
                    } else {
                        cell.setBackground(blackColor);
                    }
                } else {
                    cell.setBackground(blackColor);
                }
            }
        }
    }
}





private Timer greenDroidTimer;

private void startGreenDroidMovement() {
    greenDroidTimer = new Timer(500, (ActionEvent e) -> {
        moveGreenDroid();
    });
    greenDroidTimer.start();
}

private void stopGreenDroidMovement() {
    if (greenDroidTimer != null) {
        greenDroidTimer.stop();
    }
}

private void stopSimulation() {
    stopRedDroidMovement();
    stopGreenDroidMovement();
}

private void placeDroid() {
    // mencari sebuah sel yang merupakan jalan
    int x, y;
    do {
        x = rand.nextInt(numRows);
        y = rand.nextInt(numCols);
    } while (map[x][y] != 0);

    // menempatkan droid pada sel tersebut
    JPanel cell = (JPanel) mapPanel.getComponent(x * numCols + y);
    cell.setLayout(new BorderLayout());
    JLabel droid = new JLabel("●", SwingConstants.CENTER);
    droid.setForeground(Color.red);
    // Mengatur ukuran font droid
    Font droidFont = new Font("Arial", Font.BOLD, 40);
    droid.setFont(droidFont);
    cell.add(droid, BorderLayout.CENTER);
    redDroids.add(new Point(x, y)); // menambahkan posisi droid merah ke variabel
    mapPanel.revalidate();
    mapPanel.repaint();
   
}
private void povDroidMerah() {
    if (greenDroid == null || redDroids.isEmpty()) {
        return; // Tidak ada droid hijau atau droid merah pada peta
    }

    for (Point redDroid : redDroids) {
        int redX = redDroid.x;
        int redY = redDroid.y;

        // Cek apakah droid merah dan hijau sejajar secara vertikal atau horizontal
        if (redX == greenDroid.x || redY == greenDroid.y) {
            boolean visible = true;

            // Cek apakah ada tembok di antara droid merah dan hijau
            if (redX == greenDroid.x) { // Droid merah dan hijau sejajar secara horizontal
                int minY = Math.min(redY, greenDroid.y);
                int maxY = Math.max(redY, greenDroid.y);

                for (int y = minY + 1; y < maxY; y++) {
                    if (map[redX][y] != 0) {
                        visible = false;
                        break;
                    }
                }
            } else { // Droid merah dan hijau sejajar secara vertikal
                int minX = Math.min(redX, greenDroid.x);
                int maxX = Math.max(redX, greenDroid.x);

                for (int x = minX + 1; x < maxX; x++) {
                    if (map[x][redY] != 0) {
                        visible = false;
                        break;
                    }
                }
            }

            // Menampilkan droid hijau dengan warna sesuai visibilitas
            JPanel cell = (JPanel) mapPanel.getComponent(greenDroid.x * numCols + greenDroid.y);
            cell.removeAll();

            if (visible) {
                JLabel droid = new JLabel("●", SwingConstants.CENTER);
                droid.setForeground(Color.GREEN);
                Font droidFont = new Font("Arial", Font.BOLD, 40);
                droid.setFont(droidFont);
                cell.add(droid, BorderLayout.CENTER);
                cell.setBackground(null); // Menghapus latar belakang sel
            } else {
                cell.setBackground(Color.WHITE);
            }

            mapPanel.revalidate();
            mapPanel.repaint();
        } else {
            // Droid hijau tidak sejajar secara vertikal atau horizontal
            JPanel cell = (JPanel) mapPanel.getComponent(greenDroid.x * numCols + greenDroid.y);
            cell.removeAll();
            cell.setBackground(Color.WHITE);

            mapPanel.revalidate();
            mapPanel.repaint();
        }
    }
}


private void randomizeGreenDroid() {
    if (greenDroid != null) {
        JPanel cell = (JPanel) mapPanel.getComponent(greenDroid.x * numCols + greenDroid.y);
        cell.removeAll();
        greenDroid = null;
    }

    int x, y;
    do {
        x = rand.nextInt(numRows);
        y = rand.nextInt(numCols);
    } while (map[x][y] != 0);

    JPanel cell = (JPanel) mapPanel.getComponent(x * numCols + y);
    cell.setLayout(new BorderLayout());
    JLabel droid = new JLabel("●", SwingConstants.CENTER);
    droid.setForeground(Color.GREEN);
    Font droidFont = new Font("Arial", Font.BOLD, 40);
    droid.setFont(droidFont);
    cell.add(droid, BorderLayout.CENTER);
    greenDroid = new Point(x, y);
    mapPanel.revalidate();
    mapPanel.repaint();
}
  


private void placeGreenDroid() {
    if (greenDroid != null) {
        return; // already placed a green droid on the map
    }

    // find a cell that represents a path
    int x, y;
    do {
        x = rand.nextInt(numRows);
        y = rand.nextInt(numCols);
    } while (map[x][y] != 0);

    // check if the index is within the valid range
    if (x >= 0 && x < numRows && y >= 0 && y < numCols) {
        // place the green droid in the selected cell
        JPanel cell = (JPanel) mapPanel.getComponent(x * numCols + y);
        cell.setLayout(new BorderLayout());
        JLabel droid = new JLabel("●", SwingConstants.CENTER);
        droid.setForeground(Color.GREEN);
        Font droidFont = new Font("Arial", Font.BOLD, 40);
        droid.setFont(droidFont);
        cell.add(droid, BorderLayout.CENTER);
        greenDroid = new Point(x, y);
        mapPanel.revalidate();
        mapPanel.repaint();

      
    }
}


public static void main(String[] args) {
        Map map = new Map();
      map.startGreenDroidMovement();
    map.startRedDroidMovement();
   }
}















