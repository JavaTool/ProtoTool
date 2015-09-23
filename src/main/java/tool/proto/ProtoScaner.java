package tool.proto;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Map;

import com.google.common.collect.Multimap;

public class ProtoScaner {
	
	private Map<String, ProtoMessage> map;
	
	private Multimap<String, String> protos;
	
	public void scan(File file) throws Exception {
		map = new MessageIdScaner().scan(new File(file, "MessageId.proto"));
		
		if (file.isFile()) {
			scanProto(file);
		} else {
			for (File subFile : file.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					String name = pathname.getName();
					return !name.equals("MessageId.proto") && (name.endsWith(".proto") || pathname.isDirectory());
				}
				
			})) {
				scan(subFile);
			}
		}
	}
	
	private void scanProto(File file) throws Exception {
		LineNumberReader reader = new LineNumberReader(new FileReader(file));
		String protoName = file.getName().split("\\.")[0];
		ProtoMessage message = map.get(protoName);
		try {
			String line;
			boolean isReturn = false;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.contains("@return")) {
					isReturn = line.contains("@return");
				} else if (line.startsWith("import ")) {
					message.putImport(line.split(";")[0].replace("import ", "").replace("\"", "").replace(".proto", ""));
				} else if (line.startsWith("message") || line.startsWith("enum")) {
					String messageName = line.split(" ")[1].replace("{", "");
					message = map.get(messageName);
					message.setEnum(line.startsWith("enum"));
					message.setReturn(isReturn);
					isReturn = false;
					protos.put(protoName, messageName);
				} else if (message != null && line.trim().length() > 0 && !line.trim().startsWith("//")) {
					if (line.trim().startsWith("{")) {
					} else if (line.trim().startsWith("}")) {
						message = null;
					} else {
						String[] infos = line.split("=")[0].replaceAll("\t", "").split(" ");
						ProtoMessageField field = message.isEnum() ? new ProtoMessageField(infos[0], "", "required") : new ProtoMessageField(infos[2], infos[1], infos[0]);
						if (line.contains("//")) {
							field.setAnnotate(line.split("//")[1].trim());
						}
						message.addField(field);
					}
				}
			}
		} finally {
			reader.close();
		}
	}
	
	public Map<String, ProtoMessage> getMessages() {
		return map;
	}
	
	public Multimap<String, String> getProtos() {
		return protos;
	}

}
