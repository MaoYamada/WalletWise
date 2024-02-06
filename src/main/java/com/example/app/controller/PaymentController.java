package com.example.app.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.app.domain.Member;
import com.example.app.domain.Payment;
import com.example.app.mapper.PaymentMapper;
import com.example.app.service.MemberService;
import com.example.app.service.PaymentService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/payment")
public class PaymentController {

	@GetMapping("/main")
	public String main() {
		return "payment/main";
	}

	@Autowired
	private PaymentMapper paymentMapper;

	@Autowired
	private PaymentService service;

	@Autowired
	private MemberService memberService;

	@GetMapping("/payments")
	public String payment(Model model) throws Exception {
		List<Payment> payments = service.getPaymentList();
		model.addAttribute("payments", payments);
		return "payment/payments";
	}

	@GetMapping("/add")
	public String addGet(Model model) throws Exception {
		List<Member> members = memberService.getMemberList();
		model.addAttribute("members", members);
		model.addAttribute("title", "Add Payment");
		model.addAttribute("payment", new Payment());
		return "payment/add";
	}

	@PostMapping("/add")
	public String addPost(@Valid Payment payment, Errors errors, RedirectAttributes rd, Model model) {
		try {
			if (errors.hasErrors()) {
				List<Member> members = memberService.getMemberList();
				model.addAttribute("members", members);

				service.addPayment(payment);
				// debug
				System.out.println(payment.getNameId());

				return "payment/add";
			}
			Member member = memberService.getMemberById(payment.getNameId());
			String payerName = member.getName();
			service.addPayment(payment);
			rd.addFlashAttribute("StatusMessage", "Added Payment History Completed");
			rd.addFlashAttribute("NameId", payerName);
			rd.addFlashAttribute("Payment", payment.getPayment());
			rd.addFlashAttribute("Memo", payment.getMemo());
			return "redirect:/payment/addDone";
		} catch (Exception e) {
			model.addAttribute("error", "データの挿入に失敗しました。");
			return "payment/add";
		}
	}

	@GetMapping("/addDone")
	public String addDone(Payment payment, Model model, RedirectAttributes rd) {

		model.addAttribute("payment", payment);

		return "payment/addDone";
	}

	@GetMapping("/result")
	public String result(Payment payment, Model model) throws Exception {

//		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.JAPAN);

		List<Payment> paymentList = paymentMapper.paymentSum();

		// ③-(1)立替メンバーリスト
		List<String> replacementorList = new ArrayList<>();

		// ③-(2)立替金額リスト
		List<Integer> replacementAmountList = new ArrayList<>();

		System.out.println();
		System.out.println("--------------- S T A R T ------------------");
		System.out.println();

		// 立替した人のname_idとその各合計額
		for (Payment payHistory : paymentList) {
			// 立替した人の配列作成（配列の要素数＝立替した人の人数）
			String payerNameId = payHistory.getNameId();
			String payerName = payHistory.getName();
			System.out.print(payerName);
			System.out.print("(id:" + payerNameId + ")");
			replacementorList.add(payerNameId);
			// 立替金額をメンバーごとに合計
			Integer advancePrice = payHistory.getPaymentSum();
			replacementAmountList.add(advancePrice);

			// 3桁ごとにカンマ区切りでフォーマット
			/*
			 * String formattedAdvancePrice = numberFormat.format(advancePrice);
			 * System.out.println("が立て替えた額は ￥" + formattedAdvancePrice);
			 */
		}
		System.out.println();

		// グループ総人数、全体での支払合計額、単純に折半した時の額を計算
		int numberOfMembers = paymentMapper.countAll();
		int totalPayment = paymentMapper.totalPayment();
		int forOne = totalPayment / numberOfMembers;

		model.addAttribute("numberOfMembers", numberOfMembers);
		model.addAttribute("totalPayment", totalPayment);
		model.addAttribute("forOne", forOne);

		// グループ内にいる参加者の名前配列を作成
		List<Member> members = memberService.getMemberList();
		List<String> memberNameList = new ArrayList<>();
		List<Integer> memberIdList = new ArrayList<>();
		for (Member member : members) {
			// ①-(1)メンバー配列にデータ追加
			String memberName = member.getName();
			memberNameList.add(memberName);
			// ①-(2)メンバー配列name_idにデータ追加
			// "!="以降は、name_idがnullの場合に、0を仮代入しておくための式
			Integer memberId = member.getId() != null ? member.getId() : 0;
			memberIdList.add(memberId);
		}

		System.out.println("①-(1)メンバーname配列：" + memberNameList);
		System.out.println("①-(2)メンバーid配列：" + memberIdList);

		// メンバーの各支払うべき額、もしくは受取るべき額の配列を作成
		Integer[] debtList = new Integer[numberOfMembers];
		Arrays.fill(debtList, 0);
		// 3桁ごとにカンマ区切りでフォーマット
		// for (int i = 0; i < debtList.length; i++) {
		// }

		System.out.println("②-(1)立替name配列：" + replacementorList);
		System.out.println("②-(2)立替payment配列：" + replacementAmountList + "(立替金額が高い順番)");
		System.out.println("③支払い金額配列：" + Arrays.toString(debtList));
		System.out.println();

		// 支払額配列③を更新する計算式
		// 配列②の要素数を出す
		int nPayer = replacementorList.size();

		// メンバー数回す
		for (int i = 0; i < numberOfMembers; i++) {
			// 立替した人の人数回す
			for (int j = 0; j < nPayer; j++) {
				// pNIL = payername_idList = ②-(1)立替メンバー配列
				// mIL = memberIdList = ①-(2)メンバー配列name_id
				String pNIL = replacementorList.get(j);
				// System.out.println(pNIL);
				Integer mIL = memberIdList.get(i);
				// System.out.println(mIL);
				// ここでは
				if (pNIL.equals(String.valueOf(mIL))) {
					// pEPL = replacementAmountList = ③-(2)立替メンバー金額配列
					Integer pEPL = replacementAmountList.get(j);
					int sum = debtList[i] - pEPL;
					debtList[i] = sum;
				}
			}
		}

		System.out.println("③支払い額配列（更新後(1)立替金額マイナス）：" + Arrays.toString(debtList));

		for (int i = 0; i < numberOfMembers; i++) {
			debtList[i] += forOne;
		}

		System.out.println("③支払い額配列（更新後(2)一人当り支払い額プラス）：" + Arrays.toString(debtList));
		System.out.println();

		System.out.println("グループの人数計：" + numberOfMembers + "人");
		int overPrice = numberOfMembers - 1;
		System.out.println("全体の支出合計額：￥" + totalPayment);
		System.out.println("一人当たりの金額：￥" + forOne);

		int totalNumber = 0;
		for (int i = 0; i < numberOfMembers; i++) {
			totalNumber += debtList[i];
		}

		System.out.println("最大不足/余分金額：±￥" + overPrice);
		// totalNumberの値はoverPriceの範囲以内であるはず
		System.out.print("配列要素の総合計：" + totalNumber);
		// 念のためtrueかここで確認(Math.absは絶対値を返す)
		if (Math.abs(totalNumber) <= overPrice) {
			System.out.println("　<true>");
		} else {
			System.out.println("　<false>");
		}
		System.out.println();

		// 人数分、その人の登録している名前の配列を作成。
		Map<String, List<Integer>> memberMap = new HashMap<>();
		for (String memberName : memberNameList) {
			memberMap.put(memberName, new ArrayList<>());
			// デフォルトで0を追加
			List<Integer> tempList = memberMap.get(memberName);
			for (int i = 0; i < numberOfMembers; i++) {
				tempList.add(0);

			}
		}

		System.out.println("-----------計算結果を以下に表示--------------");
		System.out.println();

		for (int i = 0; i < numberOfMembers; i++) {
			// メンバーの名前を指定
			String targetMemberName = memberNameList.get(i);
			// memberMapから指定した名前のリストを取得
			List<Integer> targetList = memberMap.get(targetMemberName);
			System.out.print("支払う人「" + targetMemberName + "」の更新前のリストを表示 →　");
			System.out.println("List: " + targetList);

			for (int j = 1; j < numberOfMembers; j++) {

				int nextIndex = (i + j) % numberOfMembers;
				int countNum = 0;

				if (debtList[i] == 0 || debtList[nextIndex] == 0) {
					// 既に0なのでスキップ
					continue;
				} else if (debtList[i] == debtList[nextIndex]) {
					// 同じ金額の組み合わせなので計算できない
					continue;
				} else if (debtList[i] < 0 && debtList[nextIndex] < 0) {
					// 負の数の組み合わせなので計算できない
					continue;
				} else if (debtList[i] > 0 && debtList[nextIndex] > 0) {
					// 正の数の組み合わせなので計算できない
					continue;

				} else if (debtList[i] < debtList[nextIndex] && debtList[i] < 0) {
					continue;

					/*
					 * 修正が必要なエリア↓
					 * 
					 * System.out.println(); System.out.print(memberNameList.get(nextIndex) + "が");
					 * System.out.print(memberNameList.get(i) + "に支払う金額の計算で、");
					 * 
					 * // 計算前の数字のどちらも0ではないことを確認 System.out.print("[" + i + "]の要素" + debtList[i] +
					 * "と "); System.out.println("[" + nextIndex + "]の要素" + debtList[nextIndex] +
					 * "を比較しました");
					 * 
					 * while (debtList[i] != 0 && debtList[nextIndex] != 0) { debtList[nextIndex]--;
					 * debtList[i]++; countNum++; }
					 * 
					 * // リストに値を追加 targetList.set(nextIndex, countNum); //targetMemberName
					 * 
					 * System.out.println(); System.out.println("--更新後--"); System.out.println();
					 * System.out.println(targetMemberName+"のリストが更新されました　→　List: " + targetList);
					 * 
					 * 修正が必要なエリア↑
					 */

				} else if (debtList[i] > debtList[nextIndex] && debtList[nextIndex] < 0) {

					System.out.println();
					System.out.print(memberNameList.get(i) + "が");
					System.out.print(memberNameList.get(nextIndex) + "に支払う金額の計算で、");

					// 計算前の数字のどちらも0ではないことを確認
					System.out.print("[" + i + "]の要素：" + debtList[i] + "と ");
					System.out.println("[" + nextIndex + "]の要素：" + debtList[nextIndex] + "を比較しました");
					while (debtList[i] != 0 && debtList[nextIndex] != 0) {
						debtList[i]--;
						debtList[nextIndex]++;
						countNum++;
					}

					// リストのj番目にcountNumを足して再度格納する
					if (targetList.size() > nextIndex) {
						// リストのサイズがj以上なら要素が存在するので更新
						targetList.set(nextIndex, targetList.get(nextIndex) + countNum);
					} else {
						// リストのサイズがjより小さければ要素が存在しないので新しく追加
						targetList.add(countNum);
					}

					System.out.println();
					System.out.println("--更新後--");
					System.out.println();
					System.out.println(targetMemberName + "リストが更新されました　→　List: " + targetList);

				} else {
					System.out.println();
					System.out.println("その他の例外");
					continue;
				}

				System.out.println("この時の支払金額は ￥" + countNum);
				System.out.print("※debtListの[" + i + "]の要素：" + debtList[i] + "、 ");
				System.out.println("[" + nextIndex + "]の要素：" + debtList[nextIndex] + "に更新されました");
				System.out.println("----------------------------------------------");
			}

		}

		// 許容範囲内の誤差を取り消す
		for (

				int i = 0; i < numberOfMembers; i++) {
			if (debtList[i] <= overPrice) {
				debtList[i] = 0;
			}
		}

		System.out.println();

		List<String> resultList = new ArrayList<>();
		for (Map.Entry<String, List<Integer>> entry : memberMap.entrySet()) {
			String memberName = entry.getKey();
			List<Integer> tempList = entry.getValue();

			for (int i = 0; i < tempList.size(); i++) {
				if (tempList.get(i) != 0) {
					resultList.add(memberName + " → " + memberNameList.get(i) + "　　￥" + tempList.get(i));
					System.out.println("・" + memberName + "さんは" + memberNameList.get(i) + "さんに、￥" + tempList.get(i)
							+ "支払う必要があります");
				}
			}
		}

		System.out.println();
		System.out.println("----------- E N D --------------");

		model.addAttribute("resultList", resultList);
		return "payment/result";

	}
}