package elucent.albedo.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.stream.Collectors;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import elucent.albedo.Albedo;
import elucent.albedo.event.ShaderSelectEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ShaderUtil implements IResourceManagerReloadListener {
	
	public static int currentProgram = -1;
	public static int fastLightProgram = 0;
	public static int entityLightProgram = 0;
	
	public static void init(){
		fastLightProgram = loadProgram("/assets/"+Albedo.MODID+"/shaders/fastlight.vs","/assets/"+Albedo.MODID+"/shaders/fastlight.fs");
		entityLightProgram = loadProgram("/assets/"+Albedo.MODID+"/shaders/entitylight.vs","/assets/"+Albedo.MODID+"/shaders/entitylight.fs");
	}
	
	public static int loadProgram(String vsh, String fsh){
		int vertexShader = createShader(vsh,OpenGlHelper.GL_VERTEX_SHADER);
		int fragmentShader = createShader(fsh,OpenGlHelper.GL_FRAGMENT_SHADER);
		int program = OpenGlHelper.glCreateProgram();
		OpenGlHelper.glAttachShader(program, vertexShader);
		OpenGlHelper.glAttachShader(program, fragmentShader);
		OpenGlHelper.glLinkProgram(program);
		return program;
	}
	
	public static void useProgram(int program){
		OpenGlHelper.glUseProgram(program);
		currentProgram = program;
	}
	
	public static int createShader(String filename, int shaderType) {
        int shader = OpenGlHelper.glCreateShader(shaderType);
         
        if(shader == 0)
            return 0;
        try {
        	ARBShaderObjects.glShaderSourceARB(shader, readFileAsString(filename));
		} catch (Exception e) {
			e.printStackTrace();
		}
        OpenGlHelper.glCompileShader(shader);
        OpenGlHelper.glCompileShader(shader);
         
        if (GL20.glGetShaderi(shader, OpenGlHelper.GL_COMPILE_STATUS) == GL11.GL_FALSE)
            throw new RuntimeException("Error creating shader: " + getLogInfo(shader));
         
        return shader;
    }
     
    public static String getLogInfo(int obj) {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }
     
    public static String readFileAsString(String filename) throws Exception {
    	System.out.println("Loading shader ["+filename+"]...");
        StringBuilder source = new StringBuilder();
         
        InputStream in = ShaderUtil.class.getResourceAsStream(filename);
        
        String s = "";
        
        if (in != null){
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
				s = reader.lines().collect(Collectors.joining("\n"));
			}
        }
        return s;
    }

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		init();
	}
}
