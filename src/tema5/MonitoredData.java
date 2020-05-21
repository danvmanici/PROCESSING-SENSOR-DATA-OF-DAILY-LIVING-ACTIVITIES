package tema5;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MonitoredData {

	String start_time;
	String end_time;
	String activity;
	
	public MonitoredData(String start_time, String end_time, String activity) {
		
		this.start_time = start_time;
		this.end_time = end_time;
		this.activity = activity;
	}
	
	
	@Override
	public String toString() {
		return "MonitoredData [start_time=" + start_time + ", end_time=" + end_time + ", activity=" + activity + "]";
	}


	public static ArrayList<MonitoredData> monitor() throws IOException {
		
		String fileName = "Activities";
		List<String> list=new ArrayList();
		ArrayList<MonitoredData> monitor=new ArrayList<MonitoredData>();
		FileWriter myWriter;
		myWriter = new FileWriter("Task_1.txt");
	    try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

	        list = (ArrayList<String>) stream
	                .map(line -> line.split("\t\t")).flatMap(Arrays::stream) 
	                .collect(Collectors.toList());

	    } catch (IOException e) {

	        e.printStackTrace();
	    }
	    
	    for(int i=0; i< (list.size()-2); i+=3) {
	    	
	    	MonitoredData m=new MonitoredData(list.get(i),list.get(i+1),list.get(i+2));
	    	monitor.add(m);
	    	try {
				
				myWriter.write(m.toString()+'\n');
			   
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    myWriter.close();
		return monitor;
	  
    }  
	
	public long countDays(List<MonitoredData> monitor) {
		 long rez= monitor.stream().map(x -> x.start_time.substring(8, 10)).distinct().count();
		 FileWriter myWriter;
		 String s=""+rez;
		try {
			myWriter = new FileWriter("Task_2.txt");
			myWriter.write(s);
		    myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	     
		 return rez;
	}
	
	public Map<String, Integer> countActivities(List<MonitoredData> monitor) {
		Map<String, Integer> rez = monitor.stream().map(x -> x.activity).collect(Collectors.groupingBy(Function.identity(), Collectors.reducing(0, e -> 1, Integer::sum)));
		Path task3=Paths.get("Task_3.txt");
		try {
			Files.write(task3, () -> rez.entrySet().stream()
				    .<CharSequence>map(e -> e.getKey() + " - " + e.getValue())
				    .iterator());
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		return rez;
	}
	
	public String getActivity() {
		return activity;
	}

	
	public LocalTime localTime(long milliseconds ) throws ParseException {
		
		long days=TimeUnit.MILLISECONDS.toDays(milliseconds);
		long hours = TimeUnit.MILLISECONDS.toHours(milliseconds)-(days*24);
		long minutes =TimeUnit.MILLISECONDS.toMinutes(milliseconds)-( TimeUnit.MILLISECONDS.toHours(milliseconds)*60);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)-( TimeUnit.MILLISECONDS.toMinutes(milliseconds)*60);		
		LocalTime time = LocalTime.of((int)days, (int)hours, (int)minutes, (int)seconds);	
		return time;
		
	}
	
	public long getDifferenceOfTime() {
		long difference =0;
		String startTime = start_time;
		String endTime = end_time;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {			
			Date date1 = format.parse(startTime);
			Date date2 = format.parse(endTime);
			difference = date2.getTime() - date1.getTime();
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		return difference;
	}
	

	public Map<String, LocalTime> activityDuration(List<MonitoredData> monitor) {
		Map<String, Long> rez = monitor.stream().collect(Collectors.groupingBy(MonitoredData :: getActivity, Collectors.summingLong(MonitoredData :: getDifferenceOfTime)));
		Map<String, LocalTime> f=new HashMap<String, LocalTime>();
		for (String i : rez.keySet()) {
			try {
				f.put(i, localTime(rez.get(i)));
			} catch (ParseException e) {
				
				e.printStackTrace();
			}		      
		    }
		Path task5=Paths.get("Task_5.txt");
		try {
			Files.write(task5, () -> f.entrySet().stream()
				    .<CharSequence>map(e -> e.getKey() + " - " + e.getValue())
				    .iterator());
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		return f;
	}
	


	public static void main(String[] args) throws IOException, ParseException {
	
		
		MonitoredData m = new MonitoredData("", "", "");
		ArrayList<MonitoredData> monitor=m.monitor();
		m.countDays(monitor);
		m.countActivities(monitor);
		m.activityDuration(monitor);
		
	}
}
