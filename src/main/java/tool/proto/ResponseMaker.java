package tool.proto;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class ResponseMaker {
	
	private final Map<String, ProtoMessage> messages;
	
	public ResponseMaker(Map<String, ProtoMessage> messages) {
		this.messages = messages;
	}

	public void output(String dir, String path, String protoPath) {
		try {
			for (ProtoMessage message : messages.values()) {
				if (message.isClientProcessor()) {
					System.out.println(message.getName());
					outputResponses(message, dir, path, protoPath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void outputResponses(ProtoMessage message, String dir, String path, String protoPath) throws Exception {
		if (message.getFields().size() > 0) {
			String javaName = JavaFilesMaker.firstUpper(message.getName()) + "Response";
			File java = new File(JavaFilesMaker.checkString(dir) ? dir : "../../../CrossGateBase/src");
			java = new File(java, path);
			java.mkdirs();
			java = JavaFilesMaker.createFile(java, javaName + ".java");
			String importPre = "import " + protoPath.replaceAll("/", ".");
			
			StringBuilder builder = new StringBuilder("package ").append(path.replaceAll("/", ".")).append(";"), elementBuilder = new StringBuilder("\r\n");
			builder.append("\r\n");
			elementBuilder.append("\t").append("private ").append(JavaFilesMaker.firstUpper(message.getName())).append(".Builder builder = ").append(JavaFilesMaker.firstUpper(message.getName())).append(".newBuilder();");
			elementBuilder.append("\r\n");
			StringBuilder importBuilder = new StringBuilder(), methodBuilder = new StringBuilder(), paramBulider = new StringBuilder(), annotateBuilder = new StringBuilder();
			StringBuilder methodsBuilder = new StringBuilder();
			importBuilder.append("\r\n");
			importBuilder.append("import net.io.protocal.proto..ProtoResponse;");
			importBuilder.append("\r\n");
			importBuilder.append(importPre).append(message.getProtoName()).append("Protos.*;");
			importBuilder.append("\r\n");
			importBuilder.append("import com.google.protobuf.InvalidProtocolBufferException;");
			Map<String, String> imports = new HashMap<String, String>();
			imports.put(message.getProtoName(), message.getProtoName());
			for (ProtoMessageField field : message.getFields().values()) {
				methodBuilder.setLength(0);
				methodBuilder.append("\t").append("public void set").append(JavaFilesMaker.firstUpper(field.name)).append("(");
				String className = JavaFilesMaker.getProtoClassName(field.className);
				annotateBuilder.setLength(0);
				annotateBuilder.append("\r\n");
				annotateBuilder.append("\t").append("/**").append("\r\n");
				annotateBuilder.append("\t").append(" * ").append("\r\n");
				annotateBuilder.append("\t").append(" * @param").append("\t").append(field.name).append("\r\n");
				annotateBuilder.append("\t").append(" * ").append("\t\t\t").append(field.getAnnotate()).append("\r\n");
				annotateBuilder.append("\t").append(" */").append("\r\n");
				paramBulider.setLength(0);
				if (messages.containsKey(className)) {
					String importFile = messages.get(className).getProtoName();
					if (!imports.containsKey(importFile)) {
						imports.put(importFile, importFile);
						importBuilder.append("\r\n");
						importBuilder.append(importPre).append(importFile).append("Protos.*;");
					}
				}
				if (field.type.equals("required") || field.type.equals("optional")) {
					methodBuilder.append(className).append(" ");
					paramBulider.append("\t\t").append("builder.set").append(JavaFilesMaker.firstUpper(field.name)).append("(").append(field.name).append(");").append("\r\n");
				} else if (field.type.equals("repeated")) {
					JavaFilesMaker.addImport(imports, importBuilder, Iterable.class);
					methodBuilder.append("Iterable<").append(className).append("> ");
					paramBulider.append("\t\t").append("builder.addAll").append(JavaFilesMaker.firstUpper(field.name)).append("(").append(field.name).append(");").append("\r\n");
				}
				methodBuilder.append(field.name).append(", ");
				methodBuilder.setLength(methodBuilder.length() - 2);
				methodBuilder.append(") {").append("\r\n");
				methodBuilder.append(paramBulider.toString());
				methodBuilder.append("\t").append("}");
				methodBuilder.append("\r\n");
				methodsBuilder.append(annotateBuilder.toString()).append(methodBuilder.toString());
			}
			methodsBuilder.append("\r\n");
			methodsBuilder.append("\t").append("@Override").append("\r\n");
			methodsBuilder.append("\t").append("protected byte[] buildDatas() {").append("\r\n");
			methodsBuilder.append("\t\t").append("setSendMessageId0(").append(message.getName().equals("VO_Error") ? "\"MIVO_Error\"" : "getReceiveMessageId() + \"Resp\"").append(");");
			methodsBuilder.append("\r\n");
			methodsBuilder.append("\t\t").append("setStatus(").append(message.getName().equals("VO_Error") ? "HTTP_STATUS_LOGIC_ERROR" : "HTTP_STATUS_SUCCESS").append(");");
			methodsBuilder.append("\r\n");
			methodsBuilder.append("\t\t").append("return builder.build().toByteArray();").append("\r\n");
			methodsBuilder.append("\t").append("}").append("\r\n");
	
			methodsBuilder.append("\r\n");
			methodsBuilder.append("\t").append("@Override").append("\r\n");
			methodsBuilder.append("\t").append("public void mergeFrom(byte[] data) throws InvalidProtocolBufferException {").append("\r\n");
			methodsBuilder.append("\t\t").append("builder.mergeFrom(data);").append("\r\n");
			methodsBuilder.append("\t").append("}").append("\r\n");
			
			methodsBuilder.append("\r\n");
			methodsBuilder.append("\t").append("@Deprecated").append("\r\n");
			methodsBuilder.append("\t").append("@Override").append("\r\n");
			methodsBuilder.append("\t").append("public void setSendMessageId(String sendMessageId) {").append("\r\n");
			methodsBuilder.append("\t\t").append("throw new UnsupportedOperationException();").append("\r\n");
			methodsBuilder.append("\t").append("}").append("\r\n");
			
			builder.append(importBuilder.toString());
			builder.append("\r\n");
			builder.append("\r\n");
			builder.append("/**").append("\r\n");
			builder.append(" * This is a auto make java file, so do not modify me.").append("\r\n");
			builder.append(" * @author fuhuiyuan").append("\r\n");
			builder.append(" */").append("\r\n");
			builder.append("public class ").append(javaName).append(" extends ProtoResponse {");
			builder.append("\r\n");
			builder.append(elementBuilder.toString());
			builder.append("\r\n");
			builder.append("\t").append("public ").append(javaName).append("(Request request) {");
			builder.append("\r\n");
			builder.append("\t\t").append("super(request);");
			builder.append("\r\n");
			builder.append("\t").append("}");
			builder.append("\r\n");
			builder.append("\r\n");
			builder.append("\t").append("public ").append(JavaFilesMaker.firstUpper(message.getName())).append(" get").append(JavaFilesMaker.firstUpper(message.getName())).append("() {");
			builder.append("\r\n");
			builder.append("\t\t").append("return builder.build();");
			builder.append("\r\n");
			builder.append("\t").append("}");
			builder.append("\r\n");
			builder.append(methodsBuilder.toString());
			builder.append("\r\n");
			builder.append("}");
			builder.append("\r\n");
			
			FileWriter writer = new FileWriter(java);
			writer.append(builder.toString());
			writer.flush();
			writer.close();
		}
	}

}
