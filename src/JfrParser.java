import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import one.jfr.ClassRef;
import one.jfr.Dictionary;
import one.jfr.JfrReader;
import one.jfr.MethodRef;
import one.jfr.StackTrace;
import one.jfr.event.Event;

public class JfrParser {
//	private static final int FRAME_KERNEL = 5;

	private final JfrReader jfr;
	private final Dictionary<String> methodNames = new Dictionary<>();
	
	public JfrParser(JfrReader jfr) {
        this.jfr = jfr;
    }
	
	private String[] getMethodName(long methodId, int type) {
//        String result = methodNames.get(methodId);
//        if (result != null) {
//            return result;
//        }
		
		String[] result = {"", ""};

        MethodRef method = jfr.methods.get(methodId);
        ClassRef cls = jfr.classes.get(method.cls);
        byte[] className = jfr.symbols.get(cls.name);
        byte[] methodName = jfr.symbols.get(method.name);

        if (className == null || className.length == 0) {
            String methodStr = new String(methodName, StandardCharsets.UTF_8);
//            result = type == FRAME_KERNEL ? methodStr + "_[k]" : methodStr;
            result[1] = methodStr;
        } else {
            String classStr = new String(className, StandardCharsets.UTF_8);
            String methodStr = new String(methodName, StandardCharsets.UTF_8);
//            result = classStr + '.' + methodStr + "_[j]";
            result[0] = classStr;
            result[1] = methodStr;
        }

//        methodNames.put(methodId, result);
//        System.out.println(result);
        return result;
    }
	
//	private void hotMethods() {
//		
////		Map<String, Choice> result = choices.stream().collect(Collectors.toMap(Choice::getName, Function.identity()));
//		
//		
//		jfr.readAllEvents(one.jfr.event.ExecutionSample.class).stream()
//       // .filter(e -> e.getEventType().getName().equals("jdk.ExecutionSample"))
//        .map(e -> e.getStackTrace())
//        .filter(s -> s != null)
//        .map(s -> s.getFrames().get(0))
//        .filter(f -> f.isJavaFrame())
//        .map(f -> f.getMethod())
//        .collect(
//            Collectors.groupingBy(m -> m.getType().getName() + "." + m.getName() + " " + m.getDescriptor(),
//            Collectors.counting()))
//        .entrySet()
//        .stream()
//        .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
//        .forEach(e -> System.out.printf("%8d %s\n", e.getValue(), e.getKey()));
//	}
	
	private void parse(String outputFile) {		
		List<Event> events;
		try {
			FileWriter outputFileWriter = new FileWriter(outputFile);
			events = jfr.readAllEvents();
		
			for (Event event : events) {
				outputFileWriter.write(String.format("\n--- %d ns (%.2f%%), %d samples, %s\n", event.time, 0.0, 0, event.getClass()));
				StackTrace stackTrace = jfr.stackTraces.get(event.stackTraceId);
				if (stackTrace.methods.length > 0) {
	                long[] methods = stackTrace.methods;
	                byte[] types = stackTrace.types;
	                String[] trace = new String[methods.length];
	                for (int i = 0; i < methods.length; i++) {                    	
	//                    trace[trace.length - 1 - i] = getMethodName(methods[i], types[i]);
	                	String[] methodNames = getMethodName(methods[i], types[i]);
	                	if (methodNames[0].equals("")) {
	                		outputFileWriter.write(String.format("  [%2d] %s\n", i, methodNames[1]));
	                	}else {
	                		outputFileWriter.write(String.format("  [%2d] %s\n", i, methodNames[0] + "." + methodNames[1]));
	                	}
	                }
	            }
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("ERROR: Incorrect number of parameters.");
			System.out.println("Syntax: java -jar JfrParser.jar <jfr_file> <output_file>");
		}
		
		try ( JfrReader jfr = new JfrReader(args[0])) {				
            new JfrParser(jfr).parse(args[1]);
        } catch (IOException e) {
			e.printStackTrace();
		}
	}

}
