package simplenem12;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * NEM Parser
 * @author Anna
 *
 */
public class SimpleNem12ParserImpl implements SimpleNem12Parser{

	@Override
	public Collection<MeterRead> parseSimpleNem12(File simpleNem12File) {
		// This method will parse and read the incoming file
		BufferedReader br=null;
		String line="";
		String newLine="";
		String splitBy=",";
		MeterRead meterRead = null;
		MeterVolume meterVolume;
		DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyyMMdd");
		Collection<MeterRead> meterReads = new ArrayList<>();
		String nmi=null;

		SortedMap<LocalDate, MeterVolume> volumes = new TreeMap<>();
		try{
			br=new BufferedReader(new FileReader(simpleNem12File));
			line=br.readLine();
			if(line!=null && !line.isEmpty() && "100".equals(line)){
				while((newLine=br.readLine())!=null){
					if(!newLine.startsWith("900")){
					String[] newLineData=newLine.split(splitBy);					
					if(newLine.startsWith("200", 0)){
						if(nmi!=null){
							meterRead=new MeterRead(nmi, EnergyUnit.KWH);
							meterRead.setVolumes(volumes);
							meterReads.add(meterRead);
							volumes = new TreeMap<>();							
						}
						nmi=newLineData[1];

					}
					if(newLine.startsWith("300", 0)){
						meterVolume=new MeterVolume(new BigDecimal(newLineData[2]), (newLineData[3]=="A"?Quality.A:Quality.E));
						volumes.put(LocalDate.parse(newLineData[1],formatter), meterVolume);
					}
					
				}else{
					meterRead=new MeterRead(nmi, EnergyUnit.KWH);
					meterRead.setVolumes(volumes);
					meterReads.add(meterRead);
					break;
				}
			}			
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return meterReads;
	}

}
