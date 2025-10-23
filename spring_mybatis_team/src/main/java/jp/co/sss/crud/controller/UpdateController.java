package jp.co.sss.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.crud.entity.Department;
import jp.co.sss.crud.entity.Employee;
import jp.co.sss.crud.form.EmployeeForm;
import jp.co.sss.crud.mapper.DepartmentMapper;
import jp.co.sss.crud.mapper.EmployeeMapper;
import jp.co.sss.crud.service.SearchForDepartmentByDeptIdService;
import jp.co.sss.crud.service.SearchForEmployeesByEmpIdService;
import jp.co.sss.crud.service.UpdateEmployeeService;
import jp.co.sss.crud.util.BeanCopy;

/**
 * 社員更新コントローラー
 */
@Controller
public class UpdateController {

	/**
	 * 社員IDを基に社員情報を検索するサービス
	 */
	@Autowired
	SearchForEmployeesByEmpIdService searchForEmployeesByEmpIdService;

	/**
	 * 社員情報を更新するサービス
	 */
	@Autowired
	UpdateEmployeeService updateEmployeeService;

	/**
	 * 部署IDを基に部署情報を検索するサービス
	 */
	@Autowired
	SearchForDepartmentByDeptIdService searchForDepartmentByDeptIdService;
	
	/**
	 * Employeeテーブル用のマッパー
	 */
	@Autowired
	EmployeeMapper employeeMapper;
	
	/**
	 * Departmentテーブル用のマッパー
	 */
	@Autowired
	DepartmentMapper departmentMapper;

	/**
	 * 社員情報の変更内容入力画面を出力
	 *
	 * @param empId
	 *            社員ID
	 * @param model
	 *            モデル
	 * @return 遷移先のビュー
	 */
	@RequestMapping(path = "/update/input", method = RequestMethod.GET)
	public String inputUpdate(Integer empId, @ModelAttribute EmployeeForm employeeForm) {
		// 社員IDに紐づく社員情報を検索し、Employee型の変数に代入する
		Employee employee = employeeMapper.findByEmpId(empId);
		// 検索した社員情報をformに積め直す
		BeanCopy.copyEntityToForm(employee, employeeForm);
		// 更新確認画面のビュー名を返す
		return "update/update_input";
	}

	/**
	 * 社員情報の変更確認画面を出力
	 *
	 * @param employeeForm
	 *            変更対象の社員情報
	 * @param model
	 *            モデル
	 * @return 遷移先のビュー
	 */
	@RequestMapping(path = "/update/check", method = RequestMethod.POST)
	public String checkUpdate(@Valid @ModelAttribute EmployeeForm employeeForm, BindingResult result, Model model) {
		// 入力チェックでエラーが発生した場合
		if (result.hasErrors()) {
			// エラーがある場合は入力画面に戻る
			return "update/update_input";
		} else {
			// 部署IDから部署情報を検索する
			Department department = departmentMapper.findByDeptId(employeeForm.getDeptId());
			// 部署名をモデルに追加する
			model.addAttribute("deptName", department.getDeptName());
			// 更新確認画面のビュー名を返す
			return "update/update_check";
		}
	}

	/**
	 * 変更内容入力画面に戻る
	 *
	 * @param employeeForm 変更対象の社員情報
	 * @return 遷移先のビュー
	 */
	@RequestMapping(path = "/update/back", method = RequestMethod.POST)
	public String backInputUpdate(@ModelAttribute EmployeeForm employeeForm) {
		// 更新入力画面のビュー名を返す
		return "update/update_input";
	}

	/**
	 * 社員情報の変更実行
	 *
	 * @param employeeForm
	 *            変更対象の社員情報
	 * @return 完了画面URLへリダイレクト
	 */
	@RequestMapping(path = "/update/complete", method = RequestMethod.POST)
	public String completeUpdate(EmployeeForm employeeForm, HttpSession session) {
		// フォームの内容をEmployeeエンティティにコピー
		Employee employee = BeanCopy.copyFormToEmployee(employeeForm);
		// 権限がnullの場合、デフォルトの権限を設定
		if (employee.getAuthority() == null) {
			employee.setAuthority(1);
		}
		// 社員情報を更新する
		employeeMapper.update(employee);
		// セッションからユーザー情報を取得
		Employee user = (Employee) session.getAttribute("user");
		//ログイン中のユーザーが自分の情報を更新した場合、セッション情報も更新
		if (employee.getEmpId() == user.getEmpId()) {
			// セッションに保存されているユーザーの社員名を更新
			user.setEmpName(employee.getEmpName());
			session.setAttribute("user", user);
		}

		// 更新完了画面へリダイレクト
		return "redirect:/update/complete";
	}

	/**
	 * 社員情報の変更完了画面
	 *
	 * @return 遷移先のビュー
	 */
	@RequestMapping(path = "/update/complete", method = RequestMethod.GET)
	public String completeUpdate() {
		//  更新完了画面のビュー名を返す
		return "update/update_complete";

	}

}
