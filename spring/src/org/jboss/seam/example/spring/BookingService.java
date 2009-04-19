package org.jboss.seam.example.spring;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.log.Log;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 * Example of using the JpaDaoSupport.
 * 
 * @author Mike Youngstrom
 */
public class BookingService extends JpaDaoSupport {

	public static ThreadLocal<Boolean> currentThread = new ThreadLocal<Boolean>();

	@Logger
	private static Log logger;

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Hotel> findHotels(final String searchPattern, final int firstResult, final int maxResults) {
		logger.debug("Looking for a Hotel.");
		return getJpaTemplate().executeFind(new JpaCallback() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				return em
						.createQuery(
								"select h from Hotel h where lower(h.name) like :search or lower(h.city) like :search or lower(h.zip) like :search or lower(h.address) like :search")
						.setParameter("search", searchPattern).setMaxResults(maxResults).setFirstResult(firstResult)
						.getResultList();
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Booking> findBookingsByUsername(String username) {
		logger.debug("Finding Bookings for user {0}", username);
		return getJpaTemplate().findByNamedParams(
				"select b from Booking b where b.user.username = :username order by b.checkinDate",
				Collections.singletonMap("username", username));

	}

	@Transactional
	public void cancelBooking(Long bookingId) {
		logger.debug("Cancelling booking id: {0}", bookingId);
		if (bookingId == null) {
			throw new IllegalArgumentException("BookingId cannot be null");
		}

		Booking cancelled = getJpaTemplate().find(Booking.class, bookingId);
		if (cancelled != null) {
			getJpaTemplate().remove(cancelled);
		}
	}

	public void validateBooking(Booking booking) throws ValidationException {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1);

		if (booking.getCheckinDate().before(calendar.getTime())) {
			throw new ValidationException("Check in date must be a future date");
		} else if (!booking.getCheckinDate().before(booking.getCheckoutDate())) {
			throw new ValidationException("Check out date must be later than check in date");
		}
	}

	@Transactional
	public void bookHotel(Booking booking) throws ValidationException {
		validateBooking(booking);

		getJpaTemplate().persist(booking);
		getJpaTemplate().flush();
	}

	@Asynchronous
	@Transactional
	public void sendRegisterEmail(String username) {
		if (currentThread.get() != null) {
			throw new RuntimeException("Not really happening asyncronously");
		}
		logger.info("pretending to send email asyncronously");
		//Could be injected using spring injection just fine but wanted to test
		//the use of Expressions Asynchronously
		UserService userService = (UserService)Expressions.instance().createValueExpression("#{userService}").getValue();
		User user = userService.findUser(username);
		if (user != null) {
			logger.info("Asynchronously found User: {0}", user.getName());
			return;
		}
		throw new RuntimeException("Didn't find the user that made the asynchronous call");
	}

	@Transactional
	public Hotel findHotelById(Long hotelId) {
		if (hotelId == null) {
			throw new IllegalArgumentException("hotelId cannot be null");
		}

		return getJpaTemplate().find(Hotel.class, hotelId);
	}
}
