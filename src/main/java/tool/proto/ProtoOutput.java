package tool.proto;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

public class ProtoOutput {
	
	public static void main(String[] args) {
		try {
			String path = JavaFilesMaker.checkIndex(args, 0) ? args[0] : "/Users/hyfu/Documents/workspace/CrossGateProject/program/proto/proto_src/";
			File dir = new File(path);
			StringBuilder builder = new StringBuilder();
			for (File file : dir.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(".proto");
				}
				
			})) {
				builder.append("protoc --proto_path=").append(path).append(" --java_out=")
					.append((JavaFilesMaker.checkIndex(args, 1) ? args[1] : "/Users/hyfu/Documents/workspace/CrossGateBase/src/ "))
					.append(path).append(file.getName()).append("\r\n");
			}
			
			String cmd = builder.toString();
			System.out.println(cmd);
			File batFile = JavaFilesMaker.createFile(new File(JavaFilesMaker.checkIndex(args, 2) ? args[2] : "server_export.sh"));
			JavaFilesMaker.output(batFile, cmd);

			Runtime.getRuntime().exec("chmod 777 *.sh");
			Runtime.getRuntime().exec("sudo ./server_export.sh");
			batFile.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
