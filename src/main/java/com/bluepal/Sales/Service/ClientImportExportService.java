package com.bluepal.Sales.Service;

import com.bluepal.Sales.Entity.Client;
import com.bluepal.Sales.Repository.ClientRepo;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
public class ClientImportExportService {

    @Autowired
    private ClientRepo clientRepo;

    public void importClients(MultipartFile file) throws Exception {
    	if (file.isEmpty()) {
            throw new Exception("File is empty.");
        }
    
       try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

		Sheet sheet = workbook.getSheetAt(0);

		for (int i = 1; i <= sheet.getLastRowNum(); i++) {

			Row row = sheet.getRow(i);

			Client client = new Client();
			if (row != null) {
//				firstName
				if (row.getCell(0) != null) {
			 if (row.getCell(0).getCellType() == CellType.STRING) {
	                client.setFirstName(row.getCell(0).getStringCellValue());
	            } else if (row.getCell(0).getCellType() == CellType.NUMERIC) {
	                client.setFirstName(String.valueOf((int) row.getCell(0).getNumericCellValue()));
	            }
				}
				
//				middle Name
				if (row.getCell(1) != null) {
					 if (row.getCell(1).getCellType() == CellType.STRING) {
			                client.setMiddleName(row.getCell(1).getStringCellValue());
			            } else if (row.getCell(1).getCellType() == CellType.NUMERIC) {
			                client.setMiddleName(String.valueOf((int) row.getCell(1).getNumericCellValue()));
			            }
						}
				
//				last Name
				if (row.getCell(2) != null) {
					 if (row.getCell(2).getCellType() == CellType.STRING) {
			                client.setLastName(row.getCell(2).getStringCellValue());
			            } else if (row.getCell(2).getCellType() == CellType.NUMERIC) {
			                client.setLastName(String.valueOf((int) row.getCell(2).getNumericCellValue()));
			            }
						}
				
//			 address
				if (row.getCell(3) != null) {
			 if (row.getCell(3).getCellType() == CellType.STRING) {
	                client.setAddress(row.getCell(3).getStringCellValue());
	            } else if (row.getCell(3).getCellType() == CellType.NUMERIC) {
	            	client.setAddress(String.valueOf((int) row.getCell(3).getNumericCellValue()));
	            }
				}
				
//			 status
				if (row.getCell(4) != null) {
			 if (row.getCell(4).getCellType() == CellType.STRING) {
	                client.setStatus(row.getCell(4).getStringCellValue());
	            } else if (row.getCell(4).getCellType() == CellType.NUMERIC) {
	            	client.setStatus(String.valueOf((int) row.getCell(4).getNumericCellValue()));
	            }
				}
//			 rating
				if (row.getCell(5) != null) {
			 if (row.getCell(5).getCellType() == CellType.STRING) {
	                client.setRating(row.getCell(5).getStringCellValue());
	            } else if (row.getCell(5).getCellType() == CellType.NUMERIC) {
	            	client.setRating(String.valueOf((int) row.getCell(5).getNumericCellValue()));
	            }
				}
				
	            // Set Email1 (String)
				if (row.getCell(6) != null) {
	            if (row.getCell(6).getCellType() == CellType.STRING) {
	                client.setEmail1(row.getCell(6).getStringCellValue());
	            } else if (row.getCell(6).getCellType() == CellType.NUMERIC) {
	            	client.setEmail1(String.valueOf((int) row.getCell(6).getNumericCellValue()));
	            }
				}
				
//				email 2
				if (row.getCell(7) != null) {
		            if (row.getCell(7).getCellType() == CellType.STRING) {
		                client.setEmail2(row.getCell(7).getStringCellValue());
		            } else if (row.getCell(7).getCellType() == CellType.NUMERIC) {
		            	client.setEmail2(String.valueOf((int) row.getCell(7).getNumericCellValue()));
		            }

//	            Mobile No 1
				if (row.getCell(8) != null) {
	            if (row.getCell(8).getCellType() == CellType.STRING) {
	            	client.setMobileNo1(row.getCell(8).getStringCellValue());
	            } else if (row.getCell(8).getCellType() == CellType.NUMERIC) {
	            	client.setMobileNo1(String.valueOf((int) row.getCell(8).getNumericCellValue()));
	            }
				}
				
//	            Mobile No 2
				if (row.getCell(9) != null) {
	            if (row.getCell(9).getCellType() == CellType.STRING) {
	            	client.setMobileNo2(row.getCell(9).getStringCellValue());
	            } else if (row.getCell(9).getCellType() == CellType.NUMERIC) {
	            	client.setMobileNo2(String.valueOf((int) row.getCell(9).getNumericCellValue()));
	            }
				}
				}
			clientRepo.save(client);
			} 
		}
    }
    }
    public String exportClients() {
        List<Client> clients = clientRepo.findAll();
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("FirstName, MiddleName, LastName, Address, Status, Rating, EmailId1, EmailId2, MobileNumer1, MobileNumber2\n"); 
        clients.forEach(client -> {
            csvBuilder.append(client.getFirstName()).append(",")
                      .append(client.getMiddleName()).append(",")
                      .append(client.getLastName()).append(",")
                      .append(client.getAddress()).append(",")
                      .append(client.getStatus()).append(",")
                      .append(client.getRating()).append(",")
                      .append(client.getEmail1()).append(",")
                      .append(client.getEmail2()).append(",")
                      .append(client.getMobileNo2()).append(",")
                      .append(client.getMobileNo2()).append("\n");
        });
        return csvBuilder.toString();
    }
}