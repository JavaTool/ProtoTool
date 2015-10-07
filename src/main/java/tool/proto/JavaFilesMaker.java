package tool.proto;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class JavaFilesMaker {
	
	private static final String PATH = "cg/base/io/";
	
	public static void main(String[] args) {
		new JavaFilesMaker().output(args == null || args.length == 0 ? new String[]{"/Users/hyfu/Documents/workspace/CrossGateBase/src", "/Users/hyfu/Documents/workspace/CrossGateProject/program/proto/proto_src", ""} : args);
//		new JavaFilesMaker().output(new String[]{"D:/file/workspace/ServerIO/src", "D:/file/workspace/Prj/proto/proto_src", "", "../TankServer/src"});
	}
	
	public void output(String[] args) {
		File dir = new File((checkIndex(args, 1) ? args[1] : "."));
		ProtoScaner protoScaner = new ProtoScaner();
		try {
			protoScaner.scan(dir);
			Map<String, ProtoMessage> messages = protoScaner.getMessages();
//			new RequestMaker(messages).output(args[0], PATH + "proto");
			new RequestMaker(messages).output(args[0], PATH + "request", PATH + "proto");
			new ResponseMaker(messages).output(args[0], PATH + "request", PATH + "proto");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean checkIndex(String[] args, int index) {
		return args != null && args.length > index && args[index].length() > 0;
	}
	
	public static boolean checkString(String arg) {
		return arg != null && arg.length() > 0;
	}
	
	public static File createFile(File dir, String name) {
		File batFile = new File(dir, name);
		batFile.delete();
		try {
			batFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return batFile;
	}
	
	public static File createFile(File file) {
		file.delete();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public static void output(File batFile, String cmd) {
		if (cmd != null && cmd.length() > 0) {
			try {
				FileWriter writer = new FileWriter(batFile);
				writer.append(cmd);
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String firstUpper(String str) {
		return str.substring(0, 1).toUpperCase() + (str.length() > 1 ? str.substring(1) : "");
	}
	
	public static String firstLower(String str) {
		return str.substring(0, 1).toLowerCase() + (str.length() > 1 ? str.substring(1) : "");
	}
	
	public static String getProtoClassName(String className) {
		if (className.equals("int32") || className.equals("uint32")) {
			return "Integer";
		} else if (className.equals("int64")) {
			return "Long";
		} else if (className.equals("string")) {
			return "String";
		} else if (className.equals("bool")) {
			return "Boolean";
		} else {
			return className;
		}
	}
	
	public static void addImport(Map<String, String> imports, StringBuilder importBuilder, @SuppressWarnings("rawtypes") Class clz) {
		if (!imports.containsKey(clz.getSimpleName())) {
			imports.put(clz.getSimpleName(), clz.getSimpleName());
			importBuilder.append("import ").append(clz.getName()).append(";").append("\r\n");
		}
	}

}
