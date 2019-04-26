package com.nastech.upmureport.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nastech.upmureport.domain.dto.AttachmentDto;
import com.nastech.upmureport.domain.entity.Attachment;
import com.nastech.upmureport.domain.entity.Pdir;
import com.nastech.upmureport.domain.repository.AttachmentRepository;
import com.nastech.upmureport.domain.repository.PdirRepository;
import com.nastech.upmureport.support.Utils;

@Service
public class AttachmentService {

	private final String UPLOAD_PATH = "C:\\\\Users\\\\NASTECH\\\\Desktop\\\\attachment";
	private final String PREFIX_URL = "localhost.com";
	private static final Log LOG = LogFactory.getLog(AttachmentService.class);

	AttachmentRepository attachmentRepository;
	PdirRepository pdirRepository;

	public AttachmentService(AttachmentRepository attachmentRepository, PdirRepository pdirRepository) {
		this.attachmentRepository = attachmentRepository;
		this.pdirRepository = pdirRepository;
	}

	public AttachmentDto.AttachmentResDto saveAttachment(MultipartFile file, String pid) throws Exception {

		File destinationFile = saveFile(file);
		String fileName = file.getOriginalFilename();
		Pdir pdir = pdirRepository.findById(Utils.StrToBigInt(pid)).get();

		Attachment attachment = buildAttachment(destinationFile, fileName, pdir);

		return attachment2AttachmentResDto(attachmentRepository.save(attachment));
	}

	public List<AttachmentDto.AttachmentResDto> getAttachment(BigInteger pdirId) {

		Pdir pdir = pdirRepository.findById(pdirId).get();
		List<Attachment> attachments = attachmentRepository.findByDid(pdir);
		List<AttachmentDto.AttachmentResDto> attachmentResDtos = attachments2AttachmentResDtos(attachments);

		return attachmentResDtos;
	}
	
	public List<AttachmentDto.AttachmentResDto> deleteAttachment(String attachmentId) {
		Attachment attachment = attachmentRepository.findById(Utils.StrToBigInt(attachmentId)).get();
		
		attachment.deleteAttachment();
		
		attachmentRepository.save(attachment);
		
		return getAttachment(attachment.getDId().getDid());
	}

	public List<String> downloadAttachment(String attachmentId, HttpServletResponse resp) throws Exception {

		HttpServletResponse response = resp; // down
		byte[] fileByte = null;
//		
		Attachment attachment = attachmentRepository.findById(Utils.StrToBigInt(attachmentId)).get();

		try {
			fileByte = FileUtils.readFileToByteArray(new File(attachment.getLocalPath()));
			// response.setContentType("application/octet-stream");
			// response.setContentLength(fileByte.length);
			// 다운로드시 변경할 파일명
			// response.addHeader("Content-Disposition", "attachment; fileName=\"" +
			// URLEncoder.encode(attachment.getName(), "UTF-8") + "\";");
			// response.addHeader("Content-Transfer-Encoding", "binary");
		} catch (IOException e) {
			e.printStackTrace();
		}

		Encoder encoder = Base64.getEncoder();
		Decoder decoder = Base64.getDecoder();

		byte[] encoded = encoder.encode(fileByte);
		String encodedString = new String(encoded);

		List<String> resStrings = new ArrayList<String>();

		int encodedStringLength = encodedString.length();
		int opp = 0;
		while (true) {
			if (opp + 6000 < encodedStringLength) {
				resStrings.add(encodedString.substring(opp, opp + 6000));
				LOG.info("opp--" + opp);
				opp += 6000;
			} else {
				resStrings.add(encodedString.substring(opp, encodedStringLength));
				LOG.info("last" + opp + "last opp--" + encodedStringLength);
				break;
			}
		}

		int i = 0;

		resStrings.forEach(str -> {
			LOG.info(str.length());
		});

		LOG.info("encoded ===== " + encoded);
		LOG.info("encodedString ===== " + encodedString);
		LOG.info("encodedString.length() ===== " + encodedString.length());
		LOG.info("resStrings.size() ===== " + resStrings.size());

		return resStrings;
	}

	private Attachment buildAttachment(File destinationFile, String fileName, Pdir pdir) {
		return Attachment.builder().name(fileName).url(PREFIX_URL + fileName).localPath(destinationFile.getPath())
				.volume(destinationFile.length()).dId(pdir).build();
	}

	private File saveFile(MultipartFile file) throws Exception{
		
		// 유니크 값 생성 
		UUID uid = UUID.randomUUID();
		
		String fileName = uid.toString() + "_" +file.getOriginalFilename();
		
		//파일을 저장할 폴더 생성(년 월 일 기준)
		String savedPath = calcPath(UPLOAD_PATH);
		
		File destinationFile = new File(UPLOAD_PATH + savedPath,fileName);
		file.transferTo(destinationFile);
		
		LOG.info(file.getContentType());
		
//		String formatName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
				
		return destinationFile;
	}

	private List<AttachmentDto.AttachmentResDto> attachments2AttachmentResDtos(List<Attachment> attachments) {
		List<AttachmentDto.AttachmentResDto> attachmentResDtos = new ArrayList<AttachmentDto.AttachmentResDto>();

		attachments.forEach(attachment -> {
			AttachmentDto.AttachmentResDto attachmentResDto = AttachmentDto.AttachmentResDto.builder()
					.attachmentId(attachment.getAttachmentId()).attachmentName(attachment.getName())
					.volume(attachment.getVolume()).newDate(attachment.getNewDate())
					.contentType(attachment.getContentType()).build();

			attachmentResDtos.add(attachmentResDto);
		});

		return attachmentResDtos;
	}

	private AttachmentDto.AttachmentResDto attachment2AttachmentResDto(Attachment attachment) {

		AttachmentDto.AttachmentResDto attachmentResDto = AttachmentDto.AttachmentResDto.builder()
				.attachmentId(attachment.getAttachmentId()).attachmentName(attachment.getName())
				.volume(attachment.getVolume()).newDate(attachment.getNewDate())
				.contentType(attachment.getContentType()).build();

		return attachmentResDto;
	}

	// 폴더 생성 함수
	@SuppressWarnings("unused")
	private static String calcPath(String uploadPath) {

		Calendar cal = Calendar.getInstance();

		String yearPath = File.separator + cal.get(Calendar.YEAR);

		String monthPath = yearPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.MONTH) + 1);

		String datePath = monthPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.DATE));

		makeDir(uploadPath, yearPath, monthPath, datePath);

		LOG.info(datePath);

		return datePath;
	}// calcPath

	// 폴더 생성 함수
	private static void makeDir(String uploadPath, String... paths) {

		if (new File(uploadPath + paths[paths.length - 1]).exists()) {
			return;
		} // if

		for (String path : paths) {
			File dirPath = new File(uploadPath + path);

			if (!dirPath.exists()) {
				dirPath.mkdir();
			} // if

		} // for

	}// makeDir
}