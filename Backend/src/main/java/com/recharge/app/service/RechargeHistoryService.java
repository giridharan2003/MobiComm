package com.recharge.app.service;

import java.time.format.DateTimeFormatter;

import com.recharge.app.model.RechargeHistory;
import com.recharge.app.model.User;
import com.recharge.app.model.Plan;
import com.recharge.app.repository.PlanRepository;
import com.recharge.app.repository.RechargeHistoryRepository;
import com.recharge.app.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class RechargeHistoryService {

	@Autowired
	private RechargeHistoryRepository rechargeHistoryRepository;

	@Autowired
	private PlanRepository planRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailService emailService;

	public RechargeHistory saveRechargeHistory(RechargeHistory rechargeHistory) {
		if (rechargeHistory.getPlan() == null || rechargeHistory.getUser() == null) {
			throw new RuntimeException("Plan or User details are missing in the request.");
		}

		Long planId = rechargeHistory.getPlan().getPlanId().longValue();
		Long userId = rechargeHistory.getUser().getUserId();

		Plan plan = planRepository.findById(planId)
				.orElseThrow(() -> new RuntimeException("Plan not found with ID: " + planId));

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

		rechargeHistory.setPlan(plan);
		rechargeHistory.setUser(user);
		rechargeHistory.setRechargeDate(new Timestamp(System.currentTimeMillis()));
		rechargeHistory.setExpiryDate(null);
		rechargeHistory.setPaymentMode("Online");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formattedDate = rechargeHistory.getRechargeDate().toLocalDateTime().format(formatter);

		String subject = "Recharge Successful – ₹" + plan.getPrice() + " Plan Activated";

		// ✅ Start building the HTML email content
		StringBuilder htmlBody = new StringBuilder();

		htmlBody.append("<html><body style='font-family:Arial,sans-serif;padding:20px;background-color:#f8f9fa;'>")
		        .append("<div style='max-width:600px;margin:auto;background:#ffffff;padding:20px;border-radius:10px;box-shadow:0 0 10px rgba(0,0,0,0.1);'>")
		        .append("<h2 style='color:#28a745;text-align:center;'>Recharge Successful 🎉</h2>")
		        .append("<p style='font-size:16px;color:#333;'>Dear <b>").append(user.getName()).append("</b>,</p>")
		        .append("<p style='font-size:16px;color:#333;'>Your recharge of <b>₹").append(plan.getPrice())
		        .append("</b> for mobile number <b>").append(user.getPhoneNumber())
		        .append("</b> has been successfully processed.</p>")
		        .append("<table style='width:100%;border-collapse:collapse;margin-top:10px;'>");

		// ✅ Add only non-null values dynamically
		if (plan.getPrice() != null) {
		    htmlBody.append("<tr><td style='background:#28a745;color:#fff;padding:10px;font-weight:bold;'>Plan Amount</td>")
		            .append("<td style='padding:10px;border-bottom:1px solid #ddd;'>₹").append(plan.getPrice()).append("</td></tr>");
		}
		if (plan.getValidityDays() != null) {
		    htmlBody.append("<tr><td style='background:#28a745;color:#fff;padding:10px;font-weight:bold;'>Validity</td>")
		            .append("<td style='padding:10px;border-bottom:1px solid #ddd;'>").append(plan.getValidityDays()).append(" Days</td></tr>");
		}
		if (plan.getDataLimit() != null && !plan.getDataLimit().isEmpty()) {
		    htmlBody.append("<tr><td style='background:#28a745;color:#fff;padding:10px;font-weight:bold;'>Data</td>")
		            .append("<td style='padding:10px;border-bottom:1px solid #ddd;'>").append(plan.getDataLimit()).append("</td></tr>");
		}
		if (plan.getCall() != null) {
		    htmlBody.append("<tr><td style='background:#28a745;color:#fff;padding:10px;font-weight:bold;'>Calls</td>")
		            .append("<td style='padding:10px;border-bottom:1px solid #ddd;'>").append(plan.getCall()).append("</td></tr>");
		}
		if (plan.getSms() != null) {
		    htmlBody.append("<tr><td style='background:#28a745;color:#fff;padding:10px;font-weight:bold;'>SMS</td>")
		            .append("<td style='padding:10px;border-bottom:1px solid #ddd;'>").append(plan.getSms()).append("</td></tr>");
		}
		if (rechargeHistory.getTransactionId() != null) {
		    htmlBody.append("<tr><td style='background:#28a745;color:#fff;padding:10px;font-weight:bold;'>Transaction ID</td>")
		            .append("<td style='padding:10px;border-bottom:1px solid #ddd;'>").append(rechargeHistory.getTransactionId()).append("</td></tr>");
		}
		if (formattedDate != null) {
		    htmlBody.append("<tr><td style='background:#28a745;color:#fff;padding:10px;font-weight:bold;'>Date & Time</td>")
		            .append("<td style='padding:10px;border-bottom:1px solid #ddd;'>").append(formattedDate).append("</td></tr>");
		}

		// ✅ Close table and add support section
		htmlBody.append("</table>")
		        .append("<p style='font-size:16px;color:#333;margin-top:20px;'>Thank you for choosing our service! 🚀</p>")
		        .append("<p style='font-size:14px;color:#666;'><b>For support, contact:</b> <a href='mailto:mobiservice000@gmail.com' style='color:#28a745;'>mobiservice000@gmail.com</a></p>")
		        .append("</div></body></html>");


		emailService.sendEmail(user.getEmail(), subject, htmlBody);

		return rechargeHistoryRepository.save(rechargeHistory);
	}

	public List<RechargeHistory> getRechargeHistoryByUserId(Long userId) {
		return rechargeHistoryRepository.findByUserUserId(userId);
	}
}
