package jp.co.sss.crud.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jp.co.sss.crud.entity.Employee;
import jp.co.sss.crud.util.Constant;

/**
 * 権限認証用フィルタ
 * 
 * @author System Shared
 */
public class AdminAccountCheckFilter extends HttpFilter {

	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		// URIと送信方式を取得する
		String requestURI = request.getRequestURI();
		String requestMethod = request.getMethod();

		// 完了画面はフィルターを通過させる
		if (requestURI.contains("/complete") && requestMethod.equals("GET")) {
			chain.doFilter(request, response);
			return;
		}
		
		// セッションからユーザー情報を取得
		HttpSession session = request.getSession();
		Employee user = (Employee) session.getAttribute("user");
		// セッションユーザーのIDと権限の変数をそれぞれ初期化
		Integer userId = null;
		Integer authority = null;
		// セッションユーザーがNULLでない場合
		if (user != null) {
			// セッションユーザーからID、権限を取得して変数に代入
			userId = user.getEmpId();
			authority = user.getAuthority();
		}

		// 更新対象の社員IDをリクエストから取得
		String empIdString = request.getParameter("empId");
		Integer empId = null;

		// 社員IDがNULLでない場合
		if (empIdString != null) {
			// 社員IDを整数型に変換
			empId = Integer.valueOf(empIdString);
		}

		//フィルター通過のフラグを初期化 true:フィルター通過 false:ログイン画面へ戻す
		boolean accessFlg = false;

		// 管理者(セッションユーザーのIDが2)の場合、アクセス許可
		if (authority == Constant.ADMIN_AUTHORITY) {
			accessFlg = true;
			// ログインユーザ自身(セッションユーザのIDと変更リクエストの社員IDが一致)の画面はアクセス許可
		} else if (userId == empId) {
			accessFlg = true;
		}

		// accessFlgが立っていない場合はログイン画面へリダイレクトし、処理を終了する
		if (accessFlg == false) {
			//TODO  レスポンス情報を取得
			
			// ログイン画面へリダイレクト
			response.sendRedirect("/spring_crud");
			//処理を終了
			return;
		}

		chain.doFilter(request, response);
		return;

	}

}
