package hello;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.swing.UIManager;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileSystemView;


import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;

public class MyTrayIcon extends TrayIcon {

    private static final String IMAGE_PATH = "/Android.png";
    private static final String TOOLTIP = "Running";
    private PopupMenu popup;
    final SystemTray tray;
    private static MyTrayIcon instance;
    

    public MyTrayIcon(){
        super(createImage(IMAGE_PATH,TOOLTIP),TOOLTIP);
        popup = new PopupMenu();
        tray = SystemTray.getSystemTray();
        try {
			setup();
		} catch (AWTException e) {
			e.printStackTrace();
		}
        
        try {
            // Set System L&F
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
    } 
    catch (UnsupportedLookAndFeelException e) {
       // handle exception
    }
    catch (ClassNotFoundException e) {
       // handle exception
    }
    catch (InstantiationException e) {
       // handle exception
    }
    catch (IllegalAccessException e) {
       // handle exception
    }
    }
    
    public static MyTrayIcon getInstance(){
    	if(instance == null)
    		instance = new MyTrayIcon();
    	
    	return instance;
    }
    
    

    @PostConstruct
    private void setup() throws AWTException{ 
    	// Create a pop-up menu components
        MenuItem exitItem = new MenuItem("Exit");
        MenuItem rootItem = new MenuItem("Choose Root Folder");
        popup.add(rootItem);
        popup.add(exitItem);
        
        exitItem.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		final int exitCode = 0;
        		ExitCodeGenerator exitCodeGenerator = new ExitCodeGenerator() {

        		    @Override
        		    public int getExitCode() {
        		      return exitCode;
        		    }
        		    
        		  };
        		 
        		  tray.remove(MyTrayIcon.this);
        		  SpringApplication.exit(Application.context, exitCodeGenerator);
            }
        });
        
        rootItem.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        		jfc.setDialogTitle("Choose Root Directory...");
        		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        		int returnValue = jfc.showOpenDialog(null);
        		
        		if(returnValue == JFileChooser.APPROVE_OPTION){
        			File selectedFile = jfc.getSelectedFile();
        			System.out.println(selectedFile.getAbsolutePath());
        			FileUploadController.SetRoot(selectedFile.getAbsolutePath());
        		}
        	}
        });
        // popup.addSeparator();
        setPopupMenu(popup);
        tray.add(this);
        
        
        
    }

    protected static Image createImage(String path, String description){
    	
        URL imageURL = MyTrayIcon.class.getResource(path);
        if(imageURL == null){
            System.err.println("Failed Creating Image. Resource not found: "+path);
            return null;
        }else {
            return new ImageIcon(imageURL,description).getImage();
        }
    }
    
    
}