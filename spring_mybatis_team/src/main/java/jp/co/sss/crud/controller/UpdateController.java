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
		Employee employee = employeeMapper.findByEmpId(empId);
		BeanCopy.copyEntityToForm(employee, employeeForm);
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
		if (result.hasErrors()) {
			return "update/update_input";
		} else {
			Department department = departmentMapper.findByDeptId(employeeForm.getDeptId());
			model.addAttribute("deptName", department.getDeptName());
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
		Employee employee = BeanCopy.copyFormToEmployee(employeeForm);
		
		if (employee.getAuthority() == null) {
			employee.setAuthority(1);
		}
		
		employeeMapper.update(employee);
		Employee user = (Employee) session.getAttribute("user");
		
		if (employee.getEmpId() == user.getEmpId()) {
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
