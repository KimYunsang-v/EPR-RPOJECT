<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
</head>
<body>
	<form id="frm" name="frm" method="post" action="${pageContext.request.contextPath}/upmu">
		<table class="board_view">
			<colgroup>
				<col width="15%">
				<col width="*" />
			</colgroup>
			<caption>���� �ۼ�</caption>
			<tbody>
				<tr>
					<th scope="row">����</th>
					<td><input type="text" id="title" name="name" class="wdp_90"></input></td>
				</tr>
				<tr>
					<td colspan="2" class="view_text"><textarea rows="20"
							cols="100" title="����" id="contents" name="contents"></textarea></td>
				</tr>
			</tbody>
		</table>
		
		<button type="submit">�� �ۼ�</button>
	</form>

</body>
</html>