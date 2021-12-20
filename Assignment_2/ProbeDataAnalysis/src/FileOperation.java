import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileOperation {

	BufferedReader br = null;
	static FileWriter fw = null;

	public ArrayList<Probe> readProbeData(String filePath) throws IOException {
		String line = "";
		int limit = 300;
		ArrayList<Probe> lstProbe = new ArrayList<Probe>();
		try {
			br = new BufferedReader(new FileReader(filePath));
			//while ((line = br.readLine()) != null) {
			while (limit!= 0) {
				line = br.readLine();
				String[] strProbe = line.split(",");
				Probe probe = new Probe();
				probe.setSampleID(Integer.parseInt(strProbe[0]));
				probe.setDateTime(strProbe[1]);
				probe.setSourceCode(Integer.parseInt(strProbe[2]));
				probe.setLatitude(Double.parseDouble(strProbe[3]));
				probe.setLongitude(Double.parseDouble(strProbe[4]));
				probe.setAltitude(Double.parseDouble(strProbe[5]));
				probe.setSpeed(Double.parseDouble(strProbe[6]));
				probe.setHeading(Double.parseDouble(strProbe[7]));
				lstProbe.add(probe);
				limit--;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return lstProbe;
	}

	public ArrayList<Link> readLinkData(String filePath) throws NumberFormatException, IOException {
		ArrayList<Link> lstLink = new ArrayList<Link>();
		String line = "";
		try {
			br = new BufferedReader(new FileReader(filePath));
			while ((line = br.readLine()) != null) {
				String[] strLink = line.split(",");
				Link link = new Link();
				int length = strLink.length;

				link.setLinkPVID(strLink[0]);
				link.setRefNodeID(strLink[1]);
				link.setnRefNodeID(strLink[2]);
				link.setLength(Double.parseDouble(strLink[3]));
				link.setFunctionClass(Integer.parseInt(strLink[4]));
				link.setDirectionOfTravel(strLink[5].charAt(0));
				link.setSpeedCategory(Integer.parseInt(strLink[6]));
				link.setFromRefSpeedLimit(Integer.parseInt(strLink[7]));
				link.setToRefSpeedLimit(Integer.parseInt(strLink[8]));
				link.setFromRefNumLanes(Integer.parseInt(strLink[9]));
				link.setToRefNumLanes(strLink[10].charAt(0));
				link.setMultiDigitized(strLink[11].charAt(0));
				link.setUrban(strLink[12].charAt(0));
				link.setTimeZone(Double.parseDouble(strLink[13]));

				if (length >= 15) {

					if (!strLink[14].isEmpty()) {
						ArrayList<Double[]> lstShapeInfo = new ArrayList<>();
						String[] arrShapeInfoList = strLink[14].split("\\|");
						for (int i = 0; i < arrShapeInfoList.length; i++) {
							String[] arrShapeInfo = arrShapeInfoList[i].split("/");
							Double[] d = new Double[arrShapeInfo.length];
							for (int j = 0; j < arrShapeInfo.length; j++) {
								d[j] = Double.parseDouble(arrShapeInfo[j]);
							}
							lstShapeInfo.add(d);
						}
						link.setShapeInfo(lstShapeInfo);
					}
				}

				if (length >= 16) {

					if (!strLink[15].isEmpty()) {
						ArrayList<Double[]> lstCurvatureInfo = new ArrayList<>();
						String[] arrCurvatureInfoList = strLink[15].split("\\|");
						for (int i = 0; i < arrCurvatureInfoList.length; i++) {
							String[] arrCurvatureInfo = arrCurvatureInfoList[i].split("/");
							Double[] d = new Double[arrCurvatureInfo.length];
							for (int j = 0; j < arrCurvatureInfo.length; j++) {
								d[j] = Double.parseDouble(arrCurvatureInfo[j]);
							}
							lstCurvatureInfo.add(d);
						}
						link.setCurvatureInfo(lstCurvatureInfo);
					}
				}

				if (length >= 17) {

					if (!strLink[16].isEmpty()) {
						ArrayList<Double[]> lstSlopeInfo = new ArrayList<>();
						String[] arrSlopeInfoList = strLink[16].split("\\|");
						for (int i = 0; i < arrSlopeInfoList.length; i++) {
							String[] arrSlopeInfo = arrSlopeInfoList[i].split("/");
							Double[] d = new Double[arrSlopeInfo.length];
							for (int j = 0; j < arrSlopeInfo.length; j++) {
								d[j] = Double.parseDouble(arrSlopeInfo[j]);
							}
							lstSlopeInfo.add(d);
						}
						link.setSlopeInfo(lstSlopeInfo);
					}
				}
				lstLink.add(link);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return lstLink;
	}

	public static void writeMatchedPointsFile(Probe probe, int index) throws IOException {
		final String NEW_LINE_SEPARATOR = "\n";
		try {
			if (index == 0) {
				fw = new FileWriter("Partition6467MatchedPoints.csv", false);
				String header = "sampleID,dateTime,sourceCode,latitude,longitude,altitude,speed,heading,linkPVID,direction,distFromRef,distFromLink,slope";
				String data = String.valueOf(probe.getSampleID()) + "," + probe.getDateTime() + ","
						+ probe.getSourceCode() + "," + probe.getLatitude() + "," + probe.getLongitude() + ","
						+ probe.getAltitude() + "," + probe.getSpeed() + "," + probe.getHeading() + ","
						+ probe.getLinkPVID() + "," + probe.getProbeDirection() + "," + probe.getDistFromRef() + ","
						+ probe.getDistFromLink() + "," + probe.getSlope();

				fw.append(header);
				fw.append(NEW_LINE_SEPARATOR);
				fw.append(data);
				fw.append(NEW_LINE_SEPARATOR);
			} else {
				fw = new FileWriter("Partition6467MatchedPoints.csv", true);
				String data = String.valueOf(probe.getSampleID()) + "," + probe.getDateTime() + ","
						+ probe.getSourceCode() + "," + probe.getLatitude() + "," + probe.getLongitude() + ","
						+ probe.getAltitude() + "," + probe.getSpeed() + "," + probe.getHeading() + ","
						+ probe.getLinkPVID() + "," + probe.getProbeDirection() + "," + probe.getDistFromRef() + ","
						+ probe.getDistFromLink() + "," + probe.getSlope();

				fw.append(data);
				fw.append(NEW_LINE_SEPARATOR);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fw.flush();
			fw.close();
		}
	}

	public static void writeSlopeData(String linkID, double calculatedMean, double givenMean, int index)
			throws IOException {
		final String NEW_LINE_SEPARATOR = "\n";
		try {
			if (index == 0) {
				fw = new FileWriter("SlopeEvaluation.csv", false);
				String header = "linkPVID,calculatedMeanSlope,givenMeanSlope";
				String data = linkID + "," + String.valueOf(calculatedMean) + "," + String.valueOf(givenMean);
				fw.append(header);
				fw.append(NEW_LINE_SEPARATOR);
				fw.append(data);
				fw.append(NEW_LINE_SEPARATOR);
			} else {
				fw = new FileWriter("SlopeEvaluation.csv", true);
				String data = linkID + "," + String.valueOf(calculatedMean) + "," + String.valueOf(givenMean);
				fw.append(data);
				fw.append(NEW_LINE_SEPARATOR);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fw.flush();
			fw.close();
		}
	}
}
