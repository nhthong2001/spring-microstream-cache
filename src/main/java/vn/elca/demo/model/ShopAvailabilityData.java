package vn.elca.demo.model;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import vn.elca.demo.model.enumType.ShopAvailabilityLevel;


public class ShopAvailabilityData extends AbstractDto implements Comparable<ShopAvailabilityData> {
    private static final long serialVersionUID = 1L;

//	public static final ShopAvailabilityData UNKNOWN = new ShopAvailabilityData(ShopAvailabilityLevel.UNKNOWN, 0);
//	public static final ShopAvailabilityData NOT_VISIBLE = new ShopAvailabilityData(ShopAvailabilityLevel.NOT_VISIBLE,
//																					0);
//	public static final ShopAvailabilityData SOLD_OUT = new ShopAvailabilityData(ShopAvailabilityLevel.SOLD_OUT, 0);
//	public static final ShopAvailabilityData OUTSIDE_SALE_PERIOD = new ShopAvailabilityData(
//			ShopAvailabilityLevel.OUTSIDE_SALE_PERIOD, 0);
//
//	public static final ShopAvailabilityData FAKE_GOOD = new ShopAvailabilityData(ShopAvailabilityLevel.GOOD, 1000);
//	public static final ShopAvailabilityData OPTIONABLE = new ShopAvailabilityData(ShopAvailabilityLevel.OPTIONABLE,
//																				   1000);

    private ShopAvailabilityLevel level;
    private long quantity;
    private Long quota; // in the case of UEFA: quota is for purchase seats only
    private Long compQuota; // specific for UEFA: Quota for complimentary seats

    public ShopAvailabilityData() {
        this.id = 0;
        this.level = null;
        this.quantity = 0;
        this.quota = null;
        this.compQuota = null;
    }

    public ShopAvailabilityData(long id, ShopAvailabilityLevel level, long quantity) {
        this.id = id;
        this.level = level;
        this.quantity = quantity;
        this.quota = null;
        this.compQuota = null;
    }

    public ShopAvailabilityData(long id, ShopAvailabilityLevel level, long quantity, Long quota) {
        this.id = id;
        this.level = level;
        this.quantity = quantity;
        this.quota = quota;
        this.compQuota = null;
    }

    public ShopAvailabilityData(long id, ShopAvailabilityLevel level, long quantity, Long quota, Long compQuota) {
        this.id = id;
        this.level = level;
        this.quantity = quantity;
        this.quota = quota;
        this.compQuota = compQuota;
    }



    public ShopAvailabilityLevel getLevel() {
        return level;
    }

    public long getQuantity() {
        return quantity;
    }

    /**
     * Max items that can be sold, whatever the availability level. Will return null if no quota is set for this
     * availability.
     */
    public Long getQuota() {
        return quota;
    }

    public boolean isVisible() {
        return level.isVisible();
    }

    public boolean isVisibleExludingSalesPeriods() {
        return level.isVisibleExcludingSalesPeriods();
    }

    public boolean displayAsSoldOut() {
        return level.displayAsSoldOut();
    }

    public boolean hasQuota() {
        return quota != null && quota.longValue() > 0;
    }

    public boolean hasCompQuota() {
        return compQuota != null && compQuota.longValue() > 0;
    }

//    public static ShopAvailabilityData good(long quantity, Long quota) {
//        return new ShopAvailabilityData(ShopAvailabilityLevel.GOOD, quantity, quota);
//    }
//
//    /**
//     * Use if no quota is set.
//     */
//    public static ShopAvailabilityData good(final long quantity) {
//        return new ShopAvailabilityData(ShopAvailabilityLevel.GOOD, quantity);
//    }
//
//    public static ShopAvailabilityData limited(final long quantity, final Long quota) {
//        return new ShopAvailabilityData(ShopAvailabilityLevel.LIMITED, quantity, quota);
//    }
//
//    /**
//     * Use if no quota is set.
//     */
//    public static ShopAvailabilityData limited(final long quantity) {
//        return new ShopAvailabilityData(ShopAvailabilityLevel.LIMITED, quantity);
//    }

//	public static ShopAvailabilityData notVisible() {
//		return NOT_VISIBLE;
//	}
//
//	public static ShopAvailabilityData soldOut() {
//		return SOLD_OUT;
//	}

    /**
     * Takes min of quantity and quotas, if they are set.
     */
//    public static ShopAvailabilityData combine(final ShopAvailabilityData avail1, final ShopAvailabilityData avail2) {
//        Long quota = null;
//        final Long quota1 = avail1.getQuota();
//        final Long quota2 = avail2.getQuota();
//        if (quota1 != null && quota2 != null) {
//            quota = Long.valueOf(Math.min(quota1.longValue(), quota2.longValue()));
//        } else if (quota1 != null) {
//            quota = quota1;
//        } else if (quota2 != null) {
//            quota = quota2;
//        }
//        return new ShopAvailabilityData(min(Arrays.asList(avail1.level, avail2.level)), Math.min(avail1.quantity,
//                                                                                                 avail2.quantity), quota);
//    }

    @Override
    public int compareTo(final ShopAvailabilityData o) {
//        return ordering.compare(this, o);
        return 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (level == null ? 0 : level.hashCode());
        result = prime * result + (int) (quantity ^ quantity >>> 32);
        result = prime * result + (quota == null ? 0 : quota.hashCode());
        result = prime * result + (compQuota == null ? 0 : compQuota.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ShopAvailabilityData other = (ShopAvailabilityData) obj;
        if (id != other.id) {
            return false;
        }

        if (level != other.level) {
            return false;
        }
        if (quantity != other.quantity) {
            return false;
        }
        if (quota == null) {
            if (other.quota != null) {
                return false;
            }
        } else if (!quota.equals(other.quota)) {
            return false;
        }
        if (compQuota == null) {
            if (other.compQuota != null) {
                return false;
            }
        } else if (!compQuota.equals(other.compQuota)) {
            return false;
        }
        return true;
    }

//    public static final Function<ShopAvailabilityData, ShopAvailabilityLevel> levelFunction =
//            new Function<ShopAvailabilityData, ShopAvailabilityLevel>() {
//                @Override
//                public ShopAvailabilityLevel apply(final ShopAvailabilityData input) {
//                    return input.getLevel();
//                }
//            };
//    public static final Function<ShopAvailabilityData, Long> quantityFunction =
//            new Function<ShopAvailabilityData, Long>() {
//                @Override
//                public Long apply(final ShopAvailabilityData input) {
//                    return Long.valueOf(input.getQuantity());
//                }
//            };
//
//    // quantity first, then level; so that (GOOD;200) < (LIMITED;500)
//    public static final Ordering<ShopAvailabilityData> ordering = Ordering.natural().onResultOf(quantityFunction)
//                                                                          .compound(Ordering.natural().onResultOf(levelFunction));

    /**
     * Adds the passed availabilities, i.e. adds the availability quantities and takes the "best" availability level.
     * Quotas are not merged (null in result).
     */
//    public static final Function<Collection<ShopAvailabilityData>, ShopAvailabilityData> add() {
//        return new Function<Collection<ShopAvailabilityData>, ShopAvailabilityData>() {
//            @Override
//            public ShopAvailabilityData apply(final Collection<ShopAvailabilityData> availabilities) {
//                long total = 0;
//                ShopAvailabilityLevel availLevel = ShopAvailabilityLevel.UNKNOWN;
//                for (final ShopAvailabilityData shopAvailabilityData : availabilities) {
//                    total = total + shopAvailabilityData.getQuantity();
//                    if (shopAvailabilityData.getLevel().compareTo(availLevel) > 0) {
//                        availLevel = shopAvailabilityData.getLevel();
//                    }
//                }
//                return new ShopAvailabilityData(availLevel, total);
//            }
//        };
//    }

    public Long getCompQuota() {
        return compQuota;
    }

    public void setLevel(ShopAvailabilityLevel level) {
        this.level = level;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public void setQuota(Long quota) {
        this.quota = quota;
    }

    public void setCompQuota(Long compQuota) {
        this.compQuota = compQuota;
    }

    @Override
    public String toString() {
        return "ShopAvailabilityData{" +
               "level=" + level +
               ", quantity=" + quantity +
               ", quota=" + quota +
               ", compQuota=" + compQuota +
               '}';
    }
}
