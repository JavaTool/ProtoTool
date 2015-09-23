package tool.proto;

import java.io.File;

public class JavaFilesMaker {
	
	private static final String PATH = "cg/base/io/proto";
	
	public static void main(String[] args) {
		new JavaFilesMaker().output(args == null || args.length == 0 ? new String[]{"../../../CrossGateBase/src", "proto_src", ""} : args);
//		new JavaFilesMaker().output(new String[]{"D:/file/workspace/ServerIO/src", "D:/file/workspace/Prj/proto/proto_src", "", "../TankServer/src"});
	}
	
	public void output(String[] args) {
		File dir = new File((checkIndex(args, 1) ? args[1] : "."));
		ProtoScaner protoScaner = new ProtoScaner();
		try {
			protoScaner.scan(dir);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected static boolean checkIndex(String[] args, int index) {
		return args != null && args.length > index && args[index].length() > 0;
	}

}
