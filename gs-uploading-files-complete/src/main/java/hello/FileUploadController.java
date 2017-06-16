package hello;

import hello.storage.StorageFileNotFoundException;
import hello.storage.StorageService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import hello.storage.FileSystemStorageService;

@Controller
public class FileUploadController {
	
	public static Path rootLocation;

    public StorageService storageService;
    public static Preferences prefs;
    
    
    public static void SetRoot(String root)
    {
    	FileUploadController.rootLocation = Paths.get(root);
    	FileUploadController.prefs.put("root", root);
    	FileSystemStorageService.rootLocation = Paths.get(root);
    }

    @Autowired
    public FileUploadController(StorageService storageService) {
    	prefs = Preferences.userRoot().node(this.getClass().getName());
    	
    	String dir = prefs.get("root", "C:\\");
    	
        this.storageService = storageService;
        
        rootLocation = Paths.get(dir);
        FileSystemStorageService.rootLocation = Paths.get(dir);
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService
                .loadAll()
                .map(path ->
                        MvcUriComponentsBuilder
                                .fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
                                .build().toString())
                .collect(Collectors.toList()));
        
        //System.out.println(global.getRoot());

        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+file.getFilename()+"\"")
                .body(file);
    }
    
   
    @RequestMapping(method = RequestMethod.GET, value = "/videos/{video:.+}")
    public StreamingResponseBody stream(@PathVariable String video)
    		throws FileNotFoundException {
    	try{
    	Resource r = storageService.loadAsResource(video);
    	File videoFile = r.getFile();
    	final InputStream videoFileStream = new FileInputStream(videoFile);
    	return (os) -> {
    		readAndWrite(videoFileStream, os);
    	};
    	}catch(IOException e){
    		return null;
    	}
    }
    
    private void readAndWrite(final InputStream is, OutputStream os)
			throws IOException {
		byte[] data = new byte[8192];
		int read = 0;
		while ((read = is.read(data)) > 0) {
			os.write(data, 0, read);
		}
		os.flush();
	}
    
    @GetMapping("/getfiles")
    public @ResponseBody ItemResponse getItem(@RequestParam String folder)
    {
    	//System.out.println("Folder: " + folder.toString());
    	File root;
    	
    	if(folder == null || folder.isEmpty() || folder.equalsIgnoreCase("server"))
    		root = rootLocation.toFile();
    	else
    		root = new File(folder);
    	
    	File[] list = root.listFiles();
    	
    	ArrayList<Item>dir = new ArrayList<Item>();
        ArrayList<Item>fls = new ArrayList<Item>();
    	
    	for(File f : list)
    	{
    		Date lastModDate = new Date(f.lastModified());
            DateFormat formater = DateFormat.getDateTimeInstance();
            String date_modify = formater.format(lastModDate);
    		if(f.isDirectory())
    		{
    			File[] fbuf = f.listFiles();
    			int buf = 0;
    			if(fbuf != null)
    				buf = fbuf.length;
    			else
    				buf = 0;
    			
    			String num_item = String.valueOf(buf);
    			if(buf == 1) num_item = num_item + " item";
                else num_item = num_item + " items";
    			
    			dir.add(new Item(f.getName(),num_item,date_modify,f.getAbsolutePath(),"directory_icon"));
    		}
    		else
    		{
    			String extenstion = FilenameUtils.getExtension(f.getName());

                String lengthValue = "B";
                float length = f.length();
                if(f.length() > 1024 && f.length() < 1048576) {
                    lengthValue = "KB";
                    length = (float)f.length() / 1024;
                }
                else if(f.length() > 1048576 && f.length() < 1073741824){
                    lengthValue = "MB";
                    length = (float)f.length() / 1048576;
                }
                else if(f.length() > 1073741824){
                    lengthValue = "GB";
                    length = (float)f.length() / 1073741824;
                }
                
                fls.add(new Item(f.getName(), String.format("%.2f", length) + lengthValue, date_modify, f.getAbsolutePath(), extenstion));
    		}
    	}
    	
    	
    	Collections.sort(dir);
    	Collections.sort(fls);
    	
    	dir.addAll(fls);
    	
    
    	String path = (root.getAbsolutePath().equalsIgnoreCase(rootLocation.toFile().getAbsolutePath())) ? "server" : root.getParentFile().getAbsolutePath();
    	//System.out.println("Folder Root: " + root.getAbsolutePath() + "\n" + "Root Location: " + rootLocation.toFile().getAbsolutePath());
    	ItemResponse ir = new ItemResponse(path, dir);
    	
    	return ir;
    }
    
    @GetMapping("/makefolder")
    @ResponseBody
    public String MakeFile(@RequestParam String folder)
    {
    	try{
    		//System.out.println(folder);
    		File f = new File(folder);
    		f.mkdirs();
    		return "true";
    	}catch(Exception e){
    		return "Error: " + e.getMessage();
    	}
    	
    }
    
    @GetMapping("/deletefile")
    @ResponseBody
    public String DeleteFolder(@RequestParam String path){
    	try{
    		File f = new File(path);
    		boolean deleted = FileUtils.deleteQuietly(f);
    		return Boolean.toString(deleted);
    	}catch(Exception e)
    	{
    		return "Error: " + e.getMessage();
    	}
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    } 

}


