package Control;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.ToggleSwitch;

public class TranslateThis_UIController implements Initializable {
    
    @FXML
    private ToggleSwitch status;

    @FXML
    private Label date;

    @FXML
    private HBox online;

    @FXML
    private HBox offline;

    @FXML
    private BorderPane content;

    // Khởi tạo đối tượng function của TranslateThis_Function để gọi hàm
    TranslateThis_Function function = new TranslateThis_Function();

    // Tạo luồng
    private ScheduledExecutorService auto;

    // Tạo CountDownLach để thực hiện toàn bộ task
    private CountDownLatch latch = new CountDownLatch(1);

    // Tạo ScheduledFuture để quản lý việc thực hiện Task
    private ScheduledFuture<?> controltask;

    // Boolean kiểm tra thông báo 
    private boolean displayed = false;

    // String kiểm tra ngày
    private String previousdate;
    @FXML
    private MenuButton dictionary;
    @FXML
    private MenuButton data;
    @FXML
    private MenuButton game;

   

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Thay đổi tự động trạng thái của ToggleSwitch thông qua trạng thái kết nối
        ConnectionChangeToggleSwitch();

        //Hiển thị ngày
        DateDisplay();

        // Hiển thị content mặc định
        try
        {

            ChangeMenuDictionary(null);
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }

    }

    //Thay đổi tự động trạng thái của ToggleSwitch thông qua trạng thái kết nối
    public void ConnectionChangeToggleSwitch() {
        auto = Executors.newScheduledThreadPool(1);

        controltask = auto.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try
                {
                    // TASK
                    boolean check = function.CheckConnection();

                    // Trạng thái của Toggle Switch
                    status.setSelected(check);
                    
                    // Thông báo thay đổi trạng thái
                    NotifyToggleSwitchListeners(check);

                    // Hiển thị trạng thái trực tuyến
                    online.setVisible(check);

                    // Hiển thị trạng thái ngoại tuyến
                    offline.setVisible(!check);

                    // Nếu ở trạng thái ngoại tuyến và thông báo ngoại tuyến chưa được hiển thị
                    if(!check && !displayed){
                        displayed = true;
                        Platform.runLater(() -> {
                        OfflineAlert();
                    });
                    }
                    
                    // Nếu trạng thái trở lại trực tuyến thì reset lại thông báo ngoại tuyến
                    if (check && displayed) {
                        displayed = false;
                    }

                    // Đếm ngược về 0 sau khi hoàn tất toàn bộ Task để luồng reset lại
                    latch.countDown();
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }, 0, 10, TimeUnit.SECONDS);
    }
    
    //Thay đổi theo ngày hiện tại
    public void DateDisplay() {
        auto = Executors.newScheduledThreadPool(1);

        auto.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try
                {
                    // TASK
                    String currentdate = function.DateDisplay();

                    //So sánh ngày hôm qua với ngày hiện tại
                    if (!currentdate.equals(previousdate))
                    {
                        String datedisplay = function.DateDisplay();
                        Platform.runLater(() -> date.setText(datedisplay));
                        previousdate = currentdate;
                    }

                    latch.countDown();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, 0, 10, TimeUnit.MINUTES);
    }
    
    // Hiển thị thông báo
    private void OfflineAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("LƯU Ý HẠN CHẾ");
        alert.setHeaderText("Chế độ ngoại tuyến");
        alert.setContentText("1. Chế độ dịch văn bản sẽ không hoạt động \n"
                + "2. Hạn chế về cơ sở dữ liệu từ vựng");
        ImageView icon = new ImageView("/image/warning.png");
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
    }

    // Tạo mảng List để lưu thay đổi của ToggleSwitch
    private List<ToggleSwitchListener> listeners = new ArrayList<>();
    
    // Thêm đối tượng listener để nhận thông báo về sự thay đổi của ToggleSwitch
    public void AddToggleSwitchListener(ToggleSwitchListener listener) {
        listeners.add(listener);
    }
    
    // Lưu thay dổi trạng thái của ToggleSwitch vào phương thức OnToggleSwitchChanged
    private void NotifyToggleSwitchListeners(boolean status) {
        for (ToggleSwitchListener listener : listeners)
        {
            // Truyền cho các đối tượng listener của list listeners trạng thái thay đổi của Toggle Switch 
            listener.OnToggleSwitchChanged(status);
        }
    }
    
    // NotifyToggleSwitchListeners theo dõi trạng thái thay đổi của Toggle Switch lưu vào OnToggleSwitchChanged của interface ToggleSwitchListener. Sau đó truyền thông tin đến các đối tượng listener được khai báo trong danh sách của list listeners

    //Thay đổi trạng thái của ToggleSwitch thông qua tương tác người dùng
    @FXML
    public void UserChangeToggleSwitch(MouseEvent event) {
        // Kiểm tra có task đang chạy hay không 
        if (controltask != null)
        {
            controltask.cancel(true); // Dừng task lại
        }

        boolean currentstatus = status.isSelected();
        
        // Thông báo thay đổi trạng thái
        NotifyToggleSwitchListeners(currentstatus);

        // Thay đổi trạng thái dựa trên tương tác
        if (currentstatus)
        {
            ConnectionChangeToggleSwitch();
        } else
        {
            online.setVisible(false);
            offline.setVisible(true);
            OfflineAlert();
        }
    }

    // Thay đổi UI theo tương tác người dùng với menu
    @FXML
    private void ChangeMenuDictionary(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/TranslateThis_TuDienPage.fxml"));
        StackPane view = loader.load();
        content.setCenter(view);

        // Tạo đối tượng của translateThis_TuDienPageController
        TranslateThis_TuDienPageController TuDienPageController = loader.getController();
        
        // Truyền thông báo cho đối tượng của translateThis_TuDienPageController
        AddToggleSwitchListener(TuDienPageController);
    }

    @FXML
    private void ChangeMenuData(MouseEvent event) throws IOException {
        StackPane view = FXMLLoader.load(getClass().getResource("/UI/TranslateThis_DanhSachTuPage.fxml"));
        content.setCenter(view);
    }

    @FXML
    private void ChangeMenuHangmanGame(MouseEvent event) throws IOException {
        StackPane view = FXMLLoader.load(getClass().getResource("/UI/TranslateThis_HangManGamePage.fxml"));
        content.setCenter(view);
    }


}
