/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.model;

public class L1TaxCalculator {
	/**
	 * 战争税15%固定
	 */
	private static final int WAR_TAX_RATES = 15;

	/**
	 * 国税10%固定（地域税对割合）
	 */
	private static final int NATIONAL_TAX_RATES = 10;

	/**
	 * 税10%固定（战争税对割合）
	 */
	private static final int DIAD_TAX_RATES = 10;

	private final long _taxRatesCastle;
	private final long _taxRatesTown;
	private final long _taxRatesWar = WAR_TAX_RATES;

	/**
	 * @param merchantNpcId
	 *            计算对像商店NPCID
	 */
	public L1TaxCalculator(int merchantNpcId) {
		_taxRatesCastle = L1CastleLocation.getCastleTaxRateByNpcId(merchantNpcId);
		_taxRatesTown = L1TownLocation.getTownTaxRateByNpcid(merchantNpcId);
	}

	public long calcTotalTaxPrice(long price) {
		long taxCastle = price * _taxRatesCastle;
		long taxTown = price * _taxRatesTown;
		long taxWar = price * WAR_TAX_RATES;
		return (taxCastle + taxTown + taxWar) / 100;
	}

	// XXX 个别计算为、丸误差出。
	public long calcCastleTaxPrice(long price) {
		return (price * _taxRatesCastle) / 100 - calcNationalTaxPrice(price);
	}

	public long calcNationalTaxPrice(long price) {
		return (price * _taxRatesCastle) / 100 / (100 / NATIONAL_TAX_RATES);
	}

	public long calcTownTaxPrice(int price) {
		return (price * _taxRatesTown) / 100;
	}

	public long calcWarTaxPrice(int price) {
		return (price * _taxRatesWar) / 100;
	}

	public long calcDiadTaxPrice(long price) {
		return (price * _taxRatesWar) / 100 / (100 / DIAD_TAX_RATES);
	}

	/**
	 * 课税后价格求。
	 * 
	 * @param price
	 *            课税前价格
	 * @return 课税后价格
	 */
	public long layTax(long price) {
		return price + calcTotalTaxPrice(price);
	}
}
